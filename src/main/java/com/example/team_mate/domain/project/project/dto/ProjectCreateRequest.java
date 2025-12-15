package com.example.team_mate.domain.project.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "프로젝트 생성 요청")
public class ProjectCreateRequest {

    @Schema(description = "프로젝트 이름", example = "멋사 데모데이")
    private String projectName;

    @Schema(description = "프로젝트 카테고리", example = "동아리")
    private String category;

    @Schema(description = "프로젝트 마감일 (yyyy-MM-dd)", example = "2025-12-31")
    private LocalDate deadline;

    @Schema(description = "테마 색상(HEX 코드)", example = "#4F46E5")
    private String themeColor;
}
