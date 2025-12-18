package com.example.team_mate.domain.schedule.schedule.dto;

import com.example.team_mate.domain.schedule.schedule.entity.RepeatType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "특정 날짜의 일정 조회 응답 DTO")
public class EventDailyResponse {

    @Schema(description = "일정 ID", example = "10")
    private Long eventId;

    @Schema(description = "일정 제목", example = "멋사 데모데이 준비 회의")
    private String title;

    @Schema(description = "일정 메모 / 상세 내용", example = "발표 순서 확정 및 리허설")
    private String memo;

    @Schema(description = "시작 일시", example = "2025-11-24T19:00:00")
    private LocalDateTime startDateTime;

    @Schema(description = "종료 일시", example = "2025-11-24T21:00:00")
    private LocalDateTime endDateTime;

    @Schema(description = "하루 종일 일정 여부", example = "false")
    private boolean allDay;

    @Schema(description = "반복 유형", example = "NONE")
    private RepeatType repeatType;

    @Schema(description = "알람 설정 (시작 시각 기준 N분 전). 없으면 null", example = "15")
    private Integer alarmOffsetMinutes;

    @Schema(description = "요청한 사용자가 작성자인지 여부", example = "true")
    private boolean createdByMe;

    @Schema(description = "일정 작성자 이름(닉네임)", example = "likelion1")
    private String creatorName;

    @Schema(description = "참여자 닉네임 리스트", example = "[\"likelion1\", \"likelion2\"]")
    private List<String> participantNames;

    @Schema(description = "프로젝트 이름", example = "멋쟁이사자처럼 팀플")
    private String projectName;

    @Schema(description = "프로젝트 ID (이동용)", example = "1")
    private Long projectId;
}
