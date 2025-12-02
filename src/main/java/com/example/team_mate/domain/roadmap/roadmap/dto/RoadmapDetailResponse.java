package com.example.team_mate.domain.roadmap.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "로드맵 상세 응답 DTO")
public class RoadmapDetailResponse {

    @Schema(description = "로드맵 ID", example = "1")
    private Long id;

    @Schema(description = "단계", example = "1단계")
    private String role;

    @Schema(description = "로드맵 제목", example = "기획")
    private String title;

    @Schema(description = "마감일", example = "2025-12-31")
    private LocalDate deadline;

    @Schema(description = "진행률(0~100)", example = "60")
    private int progress;

    @Schema(description = "Task 목록")
    private List<TaskResponse> tasks;

    @Schema(description = "배정된 멤버 목록")
    private List<MemberSummaryResponse> members;
}