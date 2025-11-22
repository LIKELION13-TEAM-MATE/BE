package com.example.team_mate.domain.project.project.dto;

import com.example.team_mate.domain.project.project.entity.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@Schema(description = "프로젝트 응답")
public class ProjectResponse {

    @Schema(description = "프로젝트 ID", example = "1")
    private Long id;

    @Schema(description = "프로젝트 이름", example = "멋사 데모데이")
    private String projectName;

    @Schema(description = "프로젝트 카테고리", example = "동아리")
    private String category;

    @Schema(description = "프로젝트 마감일", example = "2025-12-31")
    private LocalDate deadline;

    @Schema(description = "테마 색상(HEX 코드)", example = "#4F46E5")
    private String themeColor;

    public static ProjectResponse from(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .category(project.getCategory())
                .deadline(project.getDeadline())
                .themeColor(project.getThemeColor())
                .build();
    }
}
