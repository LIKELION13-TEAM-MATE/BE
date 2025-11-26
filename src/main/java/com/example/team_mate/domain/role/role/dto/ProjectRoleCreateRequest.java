package com.example.team_mate.domain.role.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "프로젝트 역할 생성 요청 DTO")
public class ProjectRoleCreateRequest {

    @Schema(description = "역할 이름", example = "백엔드")
    private String roleName;
}
