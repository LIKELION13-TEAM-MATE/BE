package com.example.team_mate.domain.role.role.dto;

import com.example.team_mate.domain.schedule.schedule.entity.RepeatType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "일정 상세 조회 응답 DTO")
public class EventDetailResponse {

    @Schema(description = "일정 ID", example = "10")
    private Long id;

    @Schema(description = "제목", example = "멋사 데모데이 준비 회의")
    private String title;

    @Schema(description = "메모 / 상세 내용", example = "발표 순서 확정 및 리허설")
    private String memo;

    @Schema(description = "시작 일시", example = "2025-11-26T19:00:00")
    private LocalDateTime startDateTime;

    @Schema(description = "종료 일시", example = "2025-11-26T21:00:00")
    private LocalDateTime endDateTime;

    @Schema(description = "하루 종일 여부", example = "false")
    private boolean allDay;

    @Schema(description = "반복 유형", example = "NONE")
    private RepeatType repeatType;

    @Schema(description = "알람 (분 단위, null이면 알람 없음)", example = "30")
    private Integer alarmOffsetMinutes;

    @Schema(description = "작성자 닉네임", example = "likelion1")
    private String creatorNickname;

    @Schema(description = "참여자 닉네임 리스트", example = "[\"likelion2\", \"likelion3\"]")
    private List<String> participantNicknames;

    @Schema(description = "작성자/참여자에게만 보이게 설정 여부", example = "true")
    private boolean visibleToParticipantsOnly;
}