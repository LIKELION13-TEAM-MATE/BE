package com.example.team_mate.domain.role.role.controller;

import com.example.team_mate.domain.role.role.dto.ProjectRoleAssignmentRequest;
import com.example.team_mate.domain.role.role.dto.ProjectRoleAssignmentResponse;
import com.example.team_mate.domain.role.role.dto.ProjectRoleCreateRequest;
import com.example.team_mate.domain.role.role.dto.ProjectRoleResponse;
import com.example.team_mate.domain.role.role.entity.ProjectRole;
import com.example.team_mate.domain.role.role.entity.ProjectRoleAssignment;
import com.example.team_mate.domain.role.role.repository.ProjectRoleAssignmentRepository;
import com.example.team_mate.domain.role.role.repository.ProjectRoleRepository;
import com.example.team_mate.domain.role.role.service.ProjectRoleAssignmentService;
import com.example.team_mate.domain.role.role.service.ProjectRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "Role", description = "프로젝트 역할 관리 API")
@RequestMapping("/api/v1/projects/{projectId}/roles")
public class ProjectRoleApiController {

    private final ProjectRoleService projectRoleService;
    private final ProjectRoleAssignmentService assignmentService;
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectRoleAssignmentRepository assignmentRepository;

    // 프로젝트 역할 목록 조회
    @GetMapping
    @Operation(
            summary = "프로젝트 역할 목록 조회",
            description = "해당 프로젝트에 등록된 역할 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ProjectRoleResponse.class))
                    )
            )
    })
    public ResponseEntity<List<ProjectRoleResponse>> getRoles(
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId
    ) {
        List<ProjectRole> roles = projectRoleRepository.findByProjectId(projectId);

        List<ProjectRoleResponse> result = roles.stream()
                .map(role -> new ProjectRoleResponse(role.getId(), role.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // 역할 키워드 생성
    @PostMapping
    @Operation(
            summary = "역할 생성",
            description = "프로젝트에 새로운 역할 키워드를 등록합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = ProjectRoleResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없습니다.")
    })
    public ResponseEntity<ProjectRoleResponse> createRole(
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId,
            @RequestBody ProjectRoleCreateRequest request
    ) {
        ProjectRole role = projectRoleService.createRole(projectId, request.getRoleName());

        ProjectRoleResponse response = new ProjectRoleResponse(
                role.getId(),
                role.getName()
        );

        return ResponseEntity.ok(response);
    }

    // 멤버에게 역할 배정
    @PostMapping("/{roleId}/assign")
    @Operation(
            summary = "멤버에게 역할 배정",
            description = "특정 멤버에게 프로젝트 역할을 배정합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "배정 성공",
                    content = @Content(schema = @Schema(implementation = ProjectRoleAssignmentResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "프로젝트 / 역할 / 멤버를 찾을 수 없습니다.")
    })
    public ResponseEntity<ProjectRoleAssignmentResponse> assignRole(
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId,
            @Parameter(description = "역할 ID", example = "3")
            @PathVariable Long roleId,
            @RequestBody ProjectRoleAssignmentRequest request
    ) {
        ProjectRoleAssignment assignment =
                assignmentService.assignRole(projectId, roleId, request.getMemberId());

        ProjectRoleAssignmentResponse response = new ProjectRoleAssignmentResponse(
                assignment.getId(),
                assignment.getProjectRole().getId(),
                assignment.getProjectRole().getName(),
                assignment.getMember().getId()
        );

        return ResponseEntity.ok(response);
    }


    // 멤버에게 배정된 역할 해제
    @PostMapping("/{roleId}/unassign")
    @Operation(
            summary = "역할 배정 해제",
            description = "특정 멤버에게 배정된 프로젝트 역할을 해제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해제 성공"),
            @ApiResponse(responseCode = "404", description = "역할 / 멤버를 찾을 수 없습니다.")
    })
    public ResponseEntity<Void> unassignRole(
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId,
            @Parameter(description = "역할 ID", example = "3")
            @PathVariable Long roleId,
            @RequestBody ProjectRoleAssignmentRequest request
    ) {
        assignmentService.unassignRole(roleId, request.getMemberId());
        return ResponseEntity.ok().build();
    }
}
