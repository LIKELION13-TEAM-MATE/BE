package com.example.team_mate.domain.schedule.schedule.dto;

import com.example.team_mate.domain.schedule.schedule.entity.RepeatType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/** 폼 또는 Ajax에서 받을 생성 요청 DTO */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "일정 생성 요청 DTO")
public class EventCreateRequest {

    @Schema(description = "일정 제목", example = "정기 회의")
    private String title;

    @Schema(description = "일정 메모 / 상세 내용", example = "API 연동 일정 논의")
    private String memo;

    @Schema(description = "시작 일시", example = "2025-11-24T10:00:00")
    private LocalDateTime startDateTime;

    @Schema(description = "종료 일시", example = "2025-11-24T12:00:00")
    private LocalDateTime endDateTime;

    @Schema(description = "하루 종일 일정 여부", example = "false")
    private boolean allDay;

    @Schema(
            description = "반복 유형",
            example = "NONE"   // RepeatType enum 값 중 하나
    )
    private RepeatType repeatType = RepeatType.NONE;

    /** 10, 60, 120 분 등. 없으면 null */
    @Schema(description = "알람 설정 (시작 시각 기준 N분 전). 없으면 null", example = "60")
    private Integer alarmOffsetMinutes;

    /** 동업자(참여자)로 추가할 memberId 리스트 */
    @Schema(description = "참여자 memberId 리스트", example = "[2, 3, 4]")
    private List<Long> participantIds;

    /** 체크박스: true면 작성자+참여자만 조회 가능 */
    @Schema(description = "작성자와 참여자에게만 보이게 할지 여부", example = "true")
    private boolean visibleToParticipantsOnly;
}
