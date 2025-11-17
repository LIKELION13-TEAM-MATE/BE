package com.example.team_mate.domain.schedule.schedule.dto;

import com.example.team_mate.domain.schedule.schedule.entity.RepeatType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/** 폼 또는 Ajax에서 받을 생성 요청 DTO */
@Getter
@Setter
public class EventCreateRequest {

    private String title;
    private String memo;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private boolean allDay;

    private RepeatType repeatType = RepeatType.NONE;

    /** 10, 60, 120 분 등. 없으면 null */
    private Integer alarmOffsetMinutes;

    /** 동업자(참여자)로 추가할 memberId 리스트 */
    private List<Long> participantIds;

    /** 체크박스: true면 작성자+참여자만 조회 가능 */
    private boolean visibleToParticipantsOnly;
}
