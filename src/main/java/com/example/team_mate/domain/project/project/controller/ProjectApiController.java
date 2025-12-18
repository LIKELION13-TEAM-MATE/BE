package com.example.team_mate.domain.project.project.controller;

import com.example.team_mate.domain.project.project.dto.ProjectCreateRequest;
import com.example.team_mate.domain.project.project.dto.ProjectResponse;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@Tag(name = "Project", description = "프로젝트 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class ProjectApiController {

    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성", description = "새 프로젝트를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<ProjectResponse> createProject(
            @RequestBody ProjectCreateRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Project created = projectService.createProject(request, username);
        ProjectResponse response = ProjectResponse.from(created);

        return ResponseEntity
                .created(URI.create("/api/v1/projects/" + response.getId()))
                .body(response);
    }

    @Operation(summary = "내 프로젝트 목록 조회", description = "로그인한 사용자의 프로젝트 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectResponse.class))))
    })
    @GetMapping(
            produces = "application/json"
    )
    public ResponseEntity<List<ProjectResponse>> getMyProjects(Authentication authentication) {
        String username = authentication.getName();
        List<Project> myProjects = projectService.findMyProjects(username);

        List<ProjectResponse> responses = myProjects.stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "프로젝트 상세 조회", description = "프로젝트 한 건을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "프로젝트 없음")
    })
    @GetMapping(
            value = "/{projectId}",
            produces = "application/json"
    )
    public ResponseEntity<ProjectResponse> getProjectDetail(@PathVariable Long projectId) {
        Project project = projectService.findProjectById(projectId);
        return ResponseEntity.ok(ProjectResponse.from(project));
    }

    @Operation(summary = "프로젝트 수정", description = "프로젝트 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "프로젝트 없음")
    })
    @PutMapping(
            value = "/{projectId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long projectId,
            @RequestBody ProjectCreateRequest request
    ) {
        projectService.updateProject(projectId, request);
        Project updated = projectService.findProjectById(projectId);
        return ResponseEntity.ok(ProjectResponse.from(updated));
    }

    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"), // 204 -> 200으로 변경
            @ApiResponse(responseCode = "404", description = "프로젝트 없음")
    })
    @DeleteMapping(
            value = "/{projectId}"
    )
    public ResponseEntity<String> deleteProject(@PathVariable Long projectId) { // Void -> String으로 변경
        projectService.deleteProject(projectId);

        // 프론트엔드 JSON 파싱 에러 방지를 위해 메시지 반환
        return ResponseEntity.ok("{\"message\": \"Project Deleted\"}");
    }
}