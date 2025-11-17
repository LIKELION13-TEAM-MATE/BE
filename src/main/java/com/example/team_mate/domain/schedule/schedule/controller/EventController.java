package com.example.team_mate.domain.schedule.schedule.controller;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.service.ProjectService;
import com.example.team_mate.domain.schedule.schedule.dto.EventCreateRequest;
import com.example.team_mate.domain.schedule.schedule.dto.EventDailyResponse;
import com.example.team_mate.domain.schedule.schedule.dto.EventMonthlyDotResponse;
import com.example.team_mate.domain.schedule.schedule.dto.EventUpdateRequest;
import com.example.team_mate.domain.schedule.schedule.entity.Event;
import com.example.team_mate.domain.schedule.schedule.service.EventService;
import com.example.team_mate.domain.team.team.entity.TeamMembership;
import com.example.team_mate.domain.team.team.repository.TeamMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project/{projectId}/events")
public class EventController {

    private final EventService eventService;
    private final ProjectService projectService;
    private final TeamMembershipRepository teamMembershipRepository;

    /** 일정 탭 메인 – 달력 화면 */
    @GetMapping("/calendar")
    public String showCalendarPage(@PathVariable Long projectId,
                                   Authentication authentication,
                                   Model model) {

        Project project = projectService.findProjectById(projectId);

        // 로그인한 사용자 username (Member.username)
        String username = authentication.getName();

        // 프로젝트 멤버십 조회
        List<TeamMembership> memberships = teamMembershipRepository.findByProject(project);

        // 로그인한 나를 제외한 멤버들만 추출 → List<Member>
        List<Member> teammates = memberships.stream()
                .map(TeamMembership::getMember)
                .filter(m -> !m.getUsername().equals(username))
                .toList();

        model.addAttribute("projectId", projectId);
        model.addAttribute("category", project.getCategory());
        model.addAttribute("projectName", project.getProjectName());

        // 동업자 드롭다운에 쓸 리스트
        model.addAttribute("teamMembers", teammates);

        return "schedule/calendar";
    }

    /** 월별 이벤트 점 정보 (Ajax) */
    @GetMapping("/api/month")
    @ResponseBody
    public ResponseEntity<List<EventMonthlyDotResponse>> getMonthlyEvents(
            @PathVariable Long projectId,
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication
    ) {
        String username = authentication.getName();
        List<EventMonthlyDotResponse> dots =
                eventService.getMonthlyEvents(projectId, username, year, month);
        return ResponseEntity.ok(dots);
    }

    /** 일별 일정 리스트 (Ajax) */
    @GetMapping("/api/day")
    @ResponseBody
    public ResponseEntity<List<EventDailyResponse>> getDailyEvents(
            @PathVariable Long projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication
    ) {
        String username = authentication.getName();
        List<EventDailyResponse> events =
                eventService.getDailyEvents(projectId, username, date);
        return ResponseEntity.ok(events);
    }

    /** 일정 생성 (폼 or Ajax) */
    @PostMapping
    @ResponseBody
    public ResponseEntity<Long> createEvent(
            @PathVariable Long projectId,
            @RequestBody EventCreateRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Long eventId = eventService.createEvent(projectId, username, request);
        return ResponseEntity.ok(eventId);
    }

    /** 일정 상세 조회 (뷰로도 쓸 수 있음) */
    @GetMapping("/{eventId}")
    public String showEventDetail(
            @PathVariable Long projectId,
            @PathVariable Long eventId,
            Authentication authentication,
            Model model
    ) {
        String username = authentication.getName();
        Event event = eventService.getEventDetail(projectId, eventId, username);

        model.addAttribute("projectId", projectId);
        model.addAttribute("event", event);

        return "schedule/detail"; // 일정 상세 템플릿 이름 예시
    }

    /** 일정 수정 (Ajax 기준) */
    @PutMapping("/{eventId}")
    @ResponseBody
    public ResponseEntity<Void> updateEvent(
            @PathVariable Long projectId,
            @PathVariable Long eventId,
            @RequestBody EventUpdateRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        eventService.updateEvent(projectId, eventId, username, request);
        return ResponseEntity.ok().build();
    }

    /** 일정 삭제 */
    @DeleteMapping("/{eventId}")
    @ResponseBody
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long projectId,
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        eventService.deleteEvent(projectId, eventId, username);
        return ResponseEntity.ok().build();
    }
}
