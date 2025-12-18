package com.example.team_mate.domain.roadmap.roadmap.controller;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.repository.MemberRepository;
import com.example.team_mate.domain.project.project.dto.ProjectResponse; // Project DTO
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.service.ProjectService;
import com.example.team_mate.domain.roadmap.roadmap.dto.*; // Roadmap DTOs
import com.example.team_mate.domain.roadmap.roadmap.entity.Roadmap;
import com.example.team_mate.domain.roadmap.roadmap.entity.RoadmapMember;
import com.example.team_mate.domain.roadmap.roadmap.entity.RoadmapTask;
import com.example.team_mate.domain.roadmap.roadmap.service.RoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;
    private final ProjectService projectService;
    private final MemberRepository memberRepository;

    /** 로드맵 조회 */
    @GetMapping("/project/{projectId}/roadmap")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> showRoadmap(@PathVariable Long projectId,
                                                           @RequestParam(required = false) Long memberId,
                                                           Authentication authentication) {

        Project project = projectService.findProjectById(projectId);
        Long resolvedMemberId = resolveMemberId(memberId, authentication);
        List<Roadmap> roadmaps = roadmapService.getRoadmapsByProject(projectId);
        int totalProgress = roadmapService.getTotalProgress(projectId);

        // 단계 현황 계산
        int totalCount = roadmaps.size(); // 총 단계 수
        // 진행률 100
        long completedCount = roadmaps.stream().filter(r -> r.getProgress() == 100).count();

        // JSON 응답 생성
        Map<String, Object> response = new HashMap<>();

        // [수정] Entity -> DTO 변환 (안전)
        response.put("project", ProjectResponse.from(project));
        response.put("memberId", resolvedMemberId);

        // [수정] Roadmap Entity List -> DTO List 변환
        List<RoadmapDetailResponse> roadmapDtos = roadmaps.stream()
                .map(this::toDetailDto)
                .collect(Collectors.toList());
        response.put("roadmaps", roadmapDtos);

        response.put("totalProgress", totalProgress);
        response.put("totalCount", totalCount);
        response.put("completedCount", completedCount);

        return ResponseEntity.ok(response);
    }

    /** 로드맵 생성 form */
    @GetMapping("/project/{projectId}/roadmap/create")
    @ResponseBody
    public ResponseEntity<ProjectResponse> showCreateForm(@PathVariable Long projectId) {
        Project project = projectService.findProjectById(projectId);
        // [수정] Entity -> DTO 변환
        return ResponseEntity.ok(ProjectResponse.from(project));
    }

    /** 로드맵 생성 */
    @PostMapping("/project/{projectId}/roadmap/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createRoadmap(
            @PathVariable Long projectId,
            @RequestParam String role,
            @RequestParam String title,
            @RequestParam LocalDate deadline,
            @RequestParam(required = false) List<Long> memberIds
    ) {
        roadmapService.createRoadmap(projectId, role, title, deadline, memberIds);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Roadmap Created");
        response.put("projectId", projectId);

        return ResponseEntity.ok(response);
    }

    /** Task 추가 */
    @PostMapping("/roadmap/{roadmapId}/task/add")
    @ResponseBody
    public ResponseEntity<String> addTask(@PathVariable Long roadmapId, @RequestParam String content, @RequestParam Long projectId) {
        roadmapService.addTask(roadmapId, content);
        return ResponseEntity.ok("{\"message\": \"Task Added\"}");
    }

    /** Task 체크 토글 */
    @PostMapping("/roadmap/{taskId}/check")
    @ResponseBody
    public ResponseEntity<String> toggleTask(@PathVariable Long taskId, @RequestParam Long projectId) {
        roadmapService.toggleTask(taskId);
        return ResponseEntity.ok("{\"message\": \"Task Toggled\"}");
    }

    /** Task 주석(comment) 등록 */
    @PostMapping("/roadmap/task/{taskId}/note")
    @ResponseBody
    public ResponseEntity<String> updateTaskNote(
            @PathVariable Long taskId,
            @RequestParam String note,
            @RequestParam Long projectId
    ) {
        roadmapService.updateTaskNote(taskId, note);
        return ResponseEntity.ok("{\"message\": \"Task Note Updated\"}");
    }

    /** Task 삭제 */
    @PostMapping("/roadmap/task/{taskId}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId, @RequestParam Long projectId) {
        roadmapService.deleteTask(taskId);
        return ResponseEntity.ok("{\"message\": \"Task Deleted\"}");
    }

    /** 로드맵 수정 form */
    @GetMapping("/roadmap/{roadmapId}/edit")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> showEditForm(@PathVariable Long roadmapId, @RequestParam Long projectId) {
        Roadmap roadmap = roadmapService.getRoadmapById(roadmapId);
        Project project = projectService.findProjectById(projectId);

        Map<String, Object> response = new HashMap<>();

        // [수정] Entity -> DTO 변환
        response.put("roadmap", toDetailDto(roadmap));
        response.put("project", ProjectResponse.from(project));

        return ResponseEntity.ok(response);
    }

    /** 로드맵 수정 */
    @PostMapping("/roadmap/{roadmapId}/edit")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateRoadmap(
            @PathVariable Long roadmapId,
            @RequestParam Long projectId,
            @RequestParam String role,
            @RequestParam String title,
            @RequestParam LocalDate deadline,
            @RequestParam(required = false) List<Long> memberIds
    ) {
        roadmapService.updateRoadmap(roadmapId, role, title, deadline, memberIds);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Roadmap Updated");
        response.put("projectId", projectId);

        return ResponseEntity.ok(response);
    }

    /** 로드맵 삭제 */
    @PostMapping("/roadmap/{roadmapId}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteRoadmap(@PathVariable Long roadmapId, @RequestParam Long projectId) {
        roadmapService.deleteRoadmap(roadmapId);
        return ResponseEntity.ok("{\"message\": \"Roadmap Deleted\"}");
    }

    private Long resolveMemberId(Long memberId, Authentication authentication) {
        if (memberId != null) return memberId;

        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // 로그인 전 페이지면 null로 둠(필요하면 예외로 바꿔도 됨)
        }

        String username = authentication.getName();
        Member me = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Member not found: " + username));

        return me.getId();
    }


    // Entity -> DTO 변환 메서드들
    private RoadmapDetailResponse toDetailDto(Roadmap roadmap) {
        RoadmapDetailResponse dto = new RoadmapDetailResponse();
        dto.setId(roadmap.getId());
        dto.setRole(roadmap.getRole());
        dto.setTitle(roadmap.getTitle());
        dto.setDeadline(roadmap.getDeadline());
        dto.setProgress(roadmap.getProgress());

        // Task 변환
        List<TaskResponse> taskDtos = roadmap.getTasks().stream()
                .map(this::toTaskDto)
                .collect(Collectors.toList());
        dto.setTasks(taskDtos);

        // Member 변환
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