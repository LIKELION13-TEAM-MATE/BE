package com.example.team_mate.domain.roadmap.roadmap.controller;

import com.example.team_mate.domain.roadmap.roadmap.dto.*;
import com.example.team_mate.domain.roadmap.roadmap.entity.Roadmap;
import com.example.team_mate.domain.roadmap.roadmap.entity.RoadmapMember;
import com.example.team_mate.domain.roadmap.roadmap.entity.RoadmapTask;
import com.example.team_mate.domain.roadmap.roadmap.service.RoadmapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roadmap")
@Tag(name = "Roadmap", description = "프로젝트 로드맵 단계 및 Task 관리 API")
public class RoadmapApiController {

    private final RoadmapService roadmapService;

    // 프로젝트의 로드맵 목록 + 전체 진행률 조회
    @GetMapping("/projects/{projectId}/roadmaps")
    @Operation(
            summary = "프로젝트 로드맵 목록 조회(상세 포함)",
            description = "특정 프로젝트의 전체 로드맵 단계 목록과 각 단계의 Task/담당 멤버를 포함해 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로드맵 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "프로젝트 또는 로드맵을 찾을 수 없음")
    })
    public ResponseEntity<RoadmapListResponse> getRoadmaps(
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId
    ) {
        List<Roadmap> roadmaps = roadmapService.getRoadmapsByProject(projectId);
        int totalProgress = roadmapService.getTotalProgress(projectId);

        int totalCount = roadmaps.size();
        long completedCount = roadmaps.stream()
                .filter(r -> r.getProgress() == 100)
                .count();

        // 상세 DTO로 변환 (Summary → Detail)
        List<RoadmapDetailResponse> roadmapResponses = roadmaps.stream()
                .map(this::toDetailDto)
                .collect(Collectors.toList());

        RoadmapListResponse response = new RoadmapListResponse();
        response.setProjectId(projectId);
        response.setTotalProgress(totalProgress);
        response.setTotalCount(totalCount);
        response.setCompletedCount((int) completedCount);
        response.setRoadmaps(roadmapResponses);

        return ResponseEntity.ok(response);
    }

    // 로드맵 단건 상세 조회
    @GetMapping("/roadmaps/{roadmapId}")
    @Operation(
            summary = "로드맵 단건 상세 조회",
            description = "로드맵 기본 정보, Task 목록, 담당 멤버 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로드맵 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "로드맵을 찾을 수 없음")
    })
    public ResponseEntity<RoadmapDetailResponse> getRoadmapDetail(
            @Parameter(description = "로드맵 ID", example = "1")
            @PathVariable Long roadmapId
    ) {
        Roadmap roadmap = roadmapService.getRoadmapById(roadmapId);
        RoadmapDetailResponse response = toDetailDto(roadmap);
        return ResponseEntity.ok(response);
    }

    // 로드맵 생성
    @PostMapping("/projects/{projectId}/roadmaps")
    @Operation(
            summary = "로드맵 생성",
            description = "특정 프로젝트에 새로운 로드맵 단계를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "로드맵 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    })
    public ResponseEntity<Void> createRoadmap(
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId,
            @RequestBody RoadmapCreateRequest request
    ) {
        roadmapService.createRoadmap(
                projectId,
                request.getRole(),
                request.getTitle(),
                request.getDeadline(),
                request.getMemberIds()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 로드맵 수정
    @PutMapping("/roadmaps/{roadmapId}")
    @Operation(
            summary = "로드맵 수정",
            description = "로드맵의 단계명, 제목, 마감일, 담당 멤버를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로드맵 수정 성공"),
            @ApiResponse(responseCode = "404", description = "로드맵을 찾을 수 없음")
    })
    public ResponseEntity<Void> updateRoadmap(
            @Parameter(description = "로드맵 ID", example = "1")
            @PathVariable Long roadmapId,
            @RequestBody RoadmapUpdateRequest request
    ) {
        roadmapService.updateRoadmap(
                roadmapId,
                request.getRole(),
                request.getTitle(),
                request.getDeadline(),
                request.getMemberIds()
        );
        return ResponseEntity.ok().build();
    }

    // 로드맵 삭제
    @DeleteMapping("/roadmaps/{roadmapId}")
    @Operation(
            summary = "로드맵 삭제",
            description = "특정 로드맵 단계를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로드맵 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "로드맵을 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteRoadmap(
            @Parameter(description = "로드맵 ID", example = "1")
            @PathVariable Long roadmapId
    ) {
        roadmapService.deleteRoadmap(roadmapId);
        return ResponseEntity.noContent().build();
    }

    // Task 추가
    @PostMapping("/roadmaps/{roadmapId}/tasks")
    @Operation(
            summary = "로드맵 Task 추가",
            description = "특정 로드맵 단계에 Task를 추가합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task 추가 성공"),
            @ApiResponse(responseCode = "404", description = "로드맵을 찾을 수 없음")
    })
    public ResponseEntity<Void> addTask(
            @Parameter(description = "로드맵 ID", example = "1")
            @PathVariable Long roadmapId,
            @RequestBody TaskCreateRequest request
    ) {
        roadmapService.addTask(roadmapId, request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Task 체크 토글
    @PatchMapping("/tasks/{taskId}/toggle")
    @Operation(
            summary = "Task 완료 체크 토글",
            description = "Task 완료 여부를 변경합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task 상태 변경 성공"),
            @ApiResponse(responseCode = "404", description = "Task를 찾을 수 없음")
    })
    public ResponseEntity<Void> toggleTask(
            @Parameter(description = "Task ID", example = "1")
            @PathVariable Long taskId
    ) {
        roadmapService.toggleTask(taskId);
        return ResponseEntity.ok().build();
    }

    // Task 메모 등록/수정
    @PatchMapping("/tasks/{taskId}/note")
    @Operation(
            summary = "Task 메모 등록/수정",
            description = "Task의 메모를 등록 또는 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task 메모 수정 성공"),
            @ApiResponse(responseCode = "404", description = "Task를 찾을 수 없음")
    })
    public ResponseEntity<Void> updateTaskNote(
            @Parameter(description = "Task ID", example = "1")
            @PathVariable Long taskId,
            @RequestBody TaskNoteUpdateRequest request
    ) {
        roadmapService.updateTaskNote(taskId, request.getNote());
        return ResponseEntity.ok().build();
    }

    // Task 삭제
    @DeleteMapping("/tasks/{taskId}")
    @Operation(
            summary = "Task 삭제",
            description = "특정 Task를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "Task를 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID", example = "1")
            @PathVariable Long taskId
    ) {
        roadmapService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    // === 내부 매핑 메서드 ===

    // 상세 DTO 변환
    private RoadmapDetailResponse toDetailDto(Roadmap roadmap) {
        RoadmapDetailResponse dto = new RoadmapDetailResponse();
        dto.setId(roadmap.getId());
        dto.setRole(roadmap.getRole());
        dto.setTitle(roadmap.getTitle());
        dto.setDeadline(roadmap.getDeadline());
        dto.setProgress(roadmap.getProgress());

        // Task
        List<TaskResponse> taskDtos = roadmap.getTasks().stream()
                .map(this::toTaskDto)
                .collect(Collectors.toList());
        dto.setTasks(taskDtos);

        // 담당 멤버
        List<MemberSummaryResponse> memberDtos = roadmap.getMembers().stream()
                .map(this::toMemberDto)
                .collect(Collectors.toList());
        dto.setMembers(memberDtos);

        return dto;
    }

    private TaskResponse toTaskDto(RoadmapTask task) {
        TaskResponse dto = new TaskResponse();
        dto.setId(task.getId());
        dto.setContent(task.getContent());
        dto.setChecked(task.isChecked());
        dto.setNote(task.getNote());
        return dto;
    }

    private MemberSummaryResponse toMemberDto(RoadmapMember roadmapMember) {
        MemberSummaryResponse dto = new MemberSummaryResponse();
        dto.setMemberId(roadmapMember.getMember().getId());
        dto.setUsername(roadmapMember.getMember().getUsername());
        dto.setNickname(roadmapMember.getMember().getNickname());
        return dto;
    }

}
