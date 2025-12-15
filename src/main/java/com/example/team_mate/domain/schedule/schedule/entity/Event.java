package com.example.team_mate.domain.schedule.schedule.entity;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.project.project.entity.Project;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project_event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 프로젝트의 일정인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 제목
    @Column(nullable = false)
    private String title;

    // 메모
    @Lob
    private String memo;

    // 시작/종료 일시 (KST 기준)
    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    // 하루 종일 여부
    @Column(nullable = false)
    private boolean allDay;

    // 반복 정보
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepeatType repeatType = RepeatType.NONE;

    // 알림: 일정 몇 분 전에 알림 보낼지 (null 이면 알림 없음)
    private Integer alarmOffsetMinutes;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private Member createdBy;

    /**
     * true : 작성자 + 초대된(동업자) 멤버만 조회 가능
     * false: 해당 프로젝트 팀원 모두 조회 가능
     */
    @Column(nullable = false)
    private boolean visibleToParticipantsOnly = false;

    // 참여자 목록
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventParticipant> participants = new ArrayList<>();

    // --- 생성자/정적 팩토리 ---

    public Event(Project project,
                 String title,
                 String memo,
                 LocalDateTime startDateTime,
                 LocalDateTime endDateTime,
                 boolean allDay,
                 RepeatType repeatType,
                 Integer alarmOffsetMinutes,
                 Member createdBy,
                 boolean visibleToParticipantsOnly) {

        this.project = project;
        this.title = title;
        this.memo = memo;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.allDay = allDay;
        this.repeatType = repeatType;
        this.alarmOffsetMinutes = alarmOffsetMinutes;
        this.createdBy = createdBy;
        this.visibleToParticipantsOnly = visibleToParticipantsOnly;
    }

    // --- 비즈니스 메서드 ---

    public void update(String title,
                       String memo,
                       LocalDateTime startDateTime,
                       LocalDateTime endDateTime,
                       boolean allDay,
                       RepeatType repeatType,
                       Integer alarmOffsetMinutes,
                       boolean visibleToParticipantsOnly) {

        this.title = title;
        this.memo = memo;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.allDay = allDay;
        this.repeatType = repeatType;
        this.alarmOffsetMinutes = alarmOffsetMinutes;
        this.visibleToParticipantsOnly = visibleToParticipantsOnly;
    }

    public void addParticipant(EventParticipant participant) {
        this.participants.add(participant);
    }
}
