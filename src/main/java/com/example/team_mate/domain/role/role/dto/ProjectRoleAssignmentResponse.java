package com.example.team_mate.domain.role.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "프로젝트 역할 배정 응답 DTO")
public class ProjectRoleAssignmentResponse {

    @Schema(description = "배정 ID", example = "10")
    private Long assignmentId;

    @Schema(description = "역할 ID", example = "3")
    private Long roleId;

    @Schema(description = "역할 이름", example = "백엔드")
    private String roleName;

    @Schema(description = "멤버 ID", example = "5")
    private Long memberId;
}
