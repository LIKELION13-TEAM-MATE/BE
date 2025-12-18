package com.example.team_mate.domain.schedule.schedule.dto;

import com.example.team_mate.domain.schedule.schedule.entity.RepeatType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "일정 수정 요청 DTO")
public class EventUpdateRequest {

    @Schema(description = "일정 제목", example = "정기 회의(수정)")
    private String title;

    @Schema(description = "일정 메모 / 상세 내용", example = "회의 시간이 변경되었습니다.")
    private String memo;

    @Schema(description = "시작 일시", example = "2025-11-24T13:00:00")
    private LocalDateTime startDateTime;

    @Schema(description = "종료 일시", example = "2025-11-24T14:00:00")
    private LocalDateTime endDateTime;

    @Schema(description = "하루 종일 일정 여부", example = "false")
    private boolean allDay;

    @Schema(
            description = "반복 유형",
            example = "WEEKLY"
    )
    private RepeatType repeatType = RepeatType.NONE;

    @Schema(description = "알람 설정 (시작 시각 기준 N분 전). 없으면 null", example = "30")
    private Integer alarmOffsetMinutes;

    @Schema(description = "참여자 memberId 리스트", example = "[2, 5]")
    private List<Long> participantIds;

    @Schema(description = "작성자와 참여자에게만 보이게 할지 여부", example = "false")
    private boolean visibleToParticipantsOnly;
}
