package com.example.team_mate.domain.role.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "프로젝트 역할 배정/해제 요청 DTO")
public class ProjectRoleAssignmentRequest {

    @Schema(description = "멤버 ID", example = "5")
    private Long memberId;
}
