package com.example.team_mate.domain.schedule.schedule.service;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.repository.MemberRepository;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.repository.ProjectRepository;
import com.example.team_mate.domain.schedule.schedule.dto.EventCreateRequest;
import com.example.team_mate.domain.schedule.schedule.dto.EventDailyResponse;
import com.example.team_mate.domain.schedule.schedule.dto.EventMonthlyDotResponse;
import com.example.team_mate.domain.schedule.schedule.dto.EventUpdateRequest;
import com.example.team_mate.domain.schedule.schedule.entity.Event;
import com.example.team_mate.domain.schedule.schedule.entity.EventParticipant;
import com.example.team_mate.domain.schedule.schedule.entity.RepeatType;
import com.example.team_mate.domain.schedule.schedule.repository.EventParticipantRepository;
import com.example.team_mate.domain.schedule.schedule.repository.EventRepository;
import com.example.team_mate.domain.team.team.repository.TeamMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventParticipantRepository participantRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final TeamMembershipRepository teamMembershipRepository;

    // --- 공통 조회 ---

    private Member getMemberOrThrow(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));
    }

    private Project getProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
    }

    private boolean isProjectMember(Member member, Project project) {
        // TeamMembership 에서 확인
        return teamMembershipRepository.existsByMemberAndProject(member, project);
    }

    private boolean canViewEvent(Member member, Event event) {
        // 프로젝트 팀원이 아니면 무조건 불가
        if (!isProjectMember(member, event.getProject())) {
            return false;
        }

        if (!event.isVisibleToParticipantsOnly()) {
            return true; // 전체 팀원 공개
        }

        // 작성자 또는 참여자만 열람 가능
        if (event.getCreatedBy().getId().equals(member.getId())) {
            return true;
        }

        return participantRepository.existsByEventAndMember(event, member);
    }

    // --- 생성 ---

    @Transactional
    public Long createEvent(Long projectId, String username, EventCreateRequest request) {

        Member creator = getMemberOrThrow(username);
        Project project = getProjectOrThrow(projectId);

        if (!isProjectMember(creator, project)) {
            throw new IllegalStateException("프로젝트 팀원이 아니어서 일정을 생성할 수 없습니다.");
        }

        if (request.getStartDateTime().isAfter(request.getEndDateTime())) {
            throw new IllegalArgumentException("시작 시간이 종료 시간보다 늦을 수 없습니다.");
        }

        RepeatType repeatType = request.getRepeatType() != null ? request.getRepeatType() : RepeatType.NONE;

        Event event = new Event(
                project,
                request.getTitle(),
                request.getMemo(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                request.isAllDay(),
                repeatType,
                request.getAlarmOffsetMinutes(),
                creator,
                request.isVisibleToParticipantsOnly()
        );

        Event saved = eventRepository.save(event);

        // 참여자 저장 (동업자)
        if (request.getParticipantIds() != null) {
            for (Long memberId : request.getParticipantIds()) {
                Member participant = memberRepository.findById(memberId)
                        .orElseThrow(() -> new IllegalArgumentException("참여자를 찾을 수 없습니다."));
                // 리스트에 추가하는 방식으로 변경
                EventParticipant ep = new EventParticipant(saved, participant);
                saved.addParticipant(ep);
            }
        }

        return saved.getId();
    }

    // --- 월별 조회 (점 찍기 용) ---
    @Transactional(readOnly = true)
    public List<EventMonthlyDotResponse> getMonthlyEvents(
            Long projectId,
            String username,
            int year,
            int month
    ) {
        Member member = getMemberOrThrow(username);
        Project project = getProjectOrThrow(projectId);

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay  = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        LocalDateTime rangeStart = firstDay.atStartOfDay();
        LocalDateTime rangeEnd   = lastDay.atTime(23, 59, 59);

        // 1. 이 달과 기간이 겹치는 모든 이벤트
        List<Event> events =
                eventRepository.findByProjectAndEndDateTimeGreaterThanEqualAndStartDateTimeLessThanEqual(
                        project, rangeStart, rangeEnd
                );

        // 2. 내가 볼 수 있는 이벤트만
        List<Event> visibleEvents = events.stream()
                .filter(e -> canViewEvent(member, e))
                .toList();

        // 3. 날짜별 일정 개수 계산
        List<EventMonthlyDotResponse> result = new ArrayList<>();

        for (LocalDate d = firstDay; !d.isAfter(lastDay); d = d.plusDays(1)) {
            final LocalDate currentDate = d;

            long count = visibleEvents.stream()
                    .filter(e -> occursOnDate(e, currentDate))
                    .count();

            if (count > 0) {
                result.add(EventMonthlyDotResponse.of(currentDate, (int) count));
            }
        }

        return result;
    }


    // --- 일별 일정 리스트 조회 ---
    @Transactional(readOnly = true)
    public List<EventDailyResponse> getDailyEvents(Long projectId, String username,
                                                   LocalDate date) {

        Member member = getMemberOrThrow(username);
        Project project = getProjectOrThrow(projectId);

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd   = date.atTime(23, 59, 59);

        // 1. 이 날짜와 기간이 겹치는 이벤트들 가져오기
        List<Event> events =
                eventRepository.findByProjectAndEndDateTimeGreaterThanEqualAndStartDateTimeLessThanEqual(
                        project, dayStart, dayEnd);

        // 2. 권한 필터 + 이 날짜에 실제로 발생하는지(반복 계산) 필터
        return events.stream()
                .filter(e -> canViewEvent(member, e))
                .filter(e -> occursOnDate(e, date))
                .map(e -> {
                    // 내가 만든 일정인지 여부
                    boolean createdByMe = e.getCreatedBy().getId().equals(member.getId());

                    String creatorName = e.getCreatedBy().getNickname();

                    List<String> participantNames = e.getParticipants().stream()
                            .map(p -> p.getMember().getNickname())
                            .collect(Collectors.toList());

                    return new EventDailyResponse(
                            e.getId(),
                            e.getTitle(),
                            e.getMemo(),
                            e.getStartDateTime(),
                            e.getEndDateTime(),
                            e.isAllDay(),
                            e.getRepeatType(),
                            e.getAlarmOffsetMinutes(),
                            createdByMe,
                            creatorName,
                            participantNames,
                            project.getProjectName(),
                            project.getId()
                    );
                })
                .collect(Collectors.toList());
    }

    // --- 상세 조회 ---
    @Transactional(readOnly = true)
    public Event getEventDetail(Long projectId, Long eventId, String username) {
        Member member = getMemberOrThrow(username);
        Project project = getProjectOrThrow(projectId);

        Event event = eventRepository.findByIdAndProject(eventId, project)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        if (!canViewEvent(member, event)) {
            throw new IllegalStateException("일정을 조회할 권한이 없습니다.");
        }

        return event;
    }

    // --- 수정 ---
    @Transactional
    public void updateEvent(Long projectId, Long eventId, String username,
                            EventUpdateRequest request) {

        Member member = getMemberOrThrow(username);
        Project project = getProjectOrThrow(projectId);

        Event event = eventRepository.findByIdAndProject(eventId, project)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        // 작성자 또는 프로젝트 리더만 수정 가능
        boolean isLeader = project.getMember().getId().equals(member.getId());
        boolean isCreator = event.getCreatedBy().getId().equals(member.getId());

        if (!(isLeader || isCreator)) {
            throw new IllegalStateException("일정을 수정할 권한이 없습니다.");
        }

        if (request.getStartDateTime().isAfter(request.getEndDateTime())) {
            throw new IllegalArgumentException("시작 시간이 종료 시간보다 늦을 수 없습니다.");
        }

        event.update(
                request.getTitle(),
                request.getMemo(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                request.isAllDay(),
                request.getRepeatType() != null ? request.getRepeatType() : event.getRepeatType(),
                request.getAlarmOffsetMinutes(),
                request.isVisibleToParticipantsOnly()
        );

        // 👇 [수정됨] 참여자 목록 스마트 업데이트 (중복 에러 방지 로직)
        // 1. 요청된 ID 리스트 확보
        List<Long> newMemberIds = request.getParticipantIds() != null ? request.getParticipantIds() : new ArrayList<>();

        // 2. 삭제할 대상 제거: (기존 목록에는 있는데, 새 요청에는 없는 사람)
        event.getParticipants().removeIf(participant ->
                !newMemberIds.contains(participant.getMember().getId())
        );

        // 3. 추가할 대상 추가: (새 요청에는 있는데, 기존 목록에는 없는 사람)
        for (Long memberId : newMemberIds) {
            boolean alreadyExists = event.getParticipants().stream()
                    .anyMatch(p -> p.getMember().getId().equals(memberId));

            if (!alreadyExists) {
                Member participant = memberRepository.findById(memberId)
                        .orElseThrow(() -> new IllegalArgumentException("참여자를 찾을 수 없습니다."));
                EventParticipant newParticipant = new EventParticipant(event, participant);
                event.addParticipant(newParticipant);
            }
        }
    }

    // --- 삭제 ---
    @Transactional
    public void deleteEvent(Long projectId, Long eventId, String username) {

        Member member = getMemberOrThrow(username);
        Project project = getProjectOrThrow(projectId);

        Event event = eventRepository.findByIdAndProject(eventId, project)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        boolean isLeader = project.getMember().getId().equals(member.getId());
        boolean isCreator = event.getCreatedBy().getId().equals(member.getId());

        if (!(isLeader || isCreator)) {
            throw new IllegalStateException("일정을 삭제할 권한이 없습니다.");
        }

        // 반복 전체 삭제 (이번 occurrence만 삭제 같은 건 나중 단계)
        eventRepository.delete(event);
    }

    private boolean occursOnDate(Event event, LocalDate date) {
        LocalDate start = event.getStartDateTime().toLocalDate();
        LocalDate end = event.getEndDateTime().toLocalDate();

        // 전체 기간 밖이면 무조건 false
        if (date.isBefore(start) || date.isAfter(end)) {
            return false;
        }

        switch (event.getRepeatType()) {
            case NONE:
                // 단일 일정: 시작일에만 발생
                return date.equals(start);

            case DAILY:
                // 시작~종료 사이 모든 날짜
                return true;

            case WEEKLY: {
                long days = ChronoUnit.DAYS.between(start, date);
                return days % 7 == 0;
            }

            case BIWEEKLY: {
                long days = ChronoUnit.DAYS.between(start, date);
                return days % 14 == 0;
            }

            case MONTHLY:
                // 같은 "일(day-of-month)"에 반복 (단순 버전)
                return date.getDayOfMonth() == start.getDayOfMonth();

            case YEARLY:
                // 같은 월/일에 반복
                return date.getMonth() == start.getMonth()
                        && date.getDayOfMonth() == start.getDayOfMonth();

            default:
                return false;
        }

    }

    // --- 내 캘린더 월별 조회 (점 찍기 용) ---
    @Transactional(readOnly = true)
    public List<EventMonthlyDotResponse> getGlobalMonthlyEvents(String username, int year, int month) {
        Member member = getMemberOrThrow(username);

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        // 프로젝트 ID 없이 조회
        List<Event> myEvents = eventRepository.findGlobalMyEvents(
                member.getId(), firstDay.atStartOfDay(), lastDay.atTime(23, 59, 59)
        );

        List<EventMonthlyDotResponse> result = new ArrayList<>();
        for (LocalDate d = firstDay; !d.isAfter(lastDay); d = d.plusDays(1)) {
            final LocalDate currentDate = d;
            long count = myEvents.stream().filter(e -> occursOnDate(e, currentDate)).count();
            if (count > 0) {
                result.add(EventMonthlyDotResponse.of(currentDate, (int) count));
            }
        }
        return result;
    }

    // --- 내 캘린더 일별 일정 리스트 조회 ---
    @Transactional(readOnly = true)
    public List<EventDailyResponse> getGlobalDailyEvents(String username, LocalDate date) {
        Member member = getMemberOrThrow(username);

        List<Event> myEvents = eventRepository.findGlobalMyEvents(
                member.getId(), date.atStartOfDay(), date.atTime(23, 59, 59)
        );

        return myEvents.stream()
                .filter(e -> occursOnDate(e, date))
                .map(e -> {
                    boolean createdByMe = e.getCreatedBy().getId().equals(member.getId());
                    List<String> participants = e.getParticipants().stream()
                            .map(p -> p.getMember().getNickname()).toList();

                    return new EventDailyResponse(
                            e.getId(), e.getTitle(), e.getMemo(),
                            e.getStartDateTime(), e.getEndDateTime(), e.isAllDay(),
                            e.getRepeatType(), e.getAlarmOffsetMinutes(),
                            createdByMe, e.getCreatedBy().getNickname(), participants,
                            // [추가] 프로젝트 정보 매핑
                            e.getProject().getProjectName(),
                            e.getProject().getId()
                    );
                })
                .collect(Collectors.toList());
    }

}