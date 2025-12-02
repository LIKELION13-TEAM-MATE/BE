package com.example.team_mate.domain.roadmap.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "프로젝트 로드맵 목록 + 통계 응답 DTO")
public class RoadmapListResponse {

    @Schema(description = "프로젝트 ID", example = "1")
    private Long projectId;

    @Schema(description = "프로젝트 전체 진행률(평균)", example = "75")
    private int totalProgress;

    @Schema(description = "총 로드맵 단계 개수", example = "5")
    private int totalCount;

    @Schema(description = "진행률 100% 또는 마감일 지난 단계 개수", example = "3")
    private int completedCount;

    @Schema(description = "로드맵 상세 목록 (Task, 멤버 포함)")
    private List<RoadmapDetailResponse> roadmaps;
}