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
@Schema(description = "로드맵 생성 요청 DTO")
public class RoadmapCreateRequest {

    @Schema(description = "단계 설정", example = "1단계")
    private String role;

    @Schema(description = "로드맵 제목", example = "디자인")
    private String title;

    @Schema(description = "마감일", example = "2025-12-31")
    private LocalDate deadline;

    @Schema(description = "동업자 ID 목록", example = "[1]")
    private List<Long> memberIds;
}