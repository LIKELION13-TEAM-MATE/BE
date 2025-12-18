package com.example.team_mate.domain.project.project.controller;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.repository.MemberRepository;
import com.example.team_mate.domain.post.post.dto.PostResponse; // PostResponse DTO 사용
import com.example.team_mate.domain.post.post.entity.Post;
import com.example.team_mate.domain.post.post.service.PostService;
import com.example.team_mate.domain.project.project.dto.ProjectCreateRequest;
import com.example.team_mate.domain.project.project.dto.ProjectResponse; // ProjectResponse DTO 사용
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final PostService postService;
    private final MemberRepository memberRepository;

    /** 새 프로젝트 생성 form */
    @GetMapping("/new")
    @ResponseBody
    public ResponseEntity<ProjectCreateRequest> showCreateForm() {
        return ResponseEntity.ok(new ProjectCreateRequest());
    }

    /** 프로젝트 생성 */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createProject(@RequestBody ProjectCreateRequest request, Authentication authentication) {
        String username = authentication.getName();
        Project newProject = projectService.createProject(request, username);

        // 리다이렉트 대신 생성된 프로젝트 ID와 성공 메시지를 반환합니다.
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Project Created");
        response.put("projectId", newProject.getId());

        return ResponseEntity.ok(response);
    }

    /** 프로젝트 detail */
    @GetMapping("/detail/{projectId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> showProjectDetail(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        Map<String, Object> response = new HashMap<>();

        Project project = projectService.findProjectById(projectId);
        response.put("project", ProjectResponse.from(project));

        List<Post> posts = postService.getPostsByProject(projectId);
        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());
        response.put("posts", postResponses);

        String username = authentication.getName();
        Member me = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Member not found: " + username));
        response.put("memberId", me.getId());

        return ResponseEntity.ok(response);
    }

    /** 프로젝트 수정 form */
    @GetMapping("/edit/{projectId}")
    @ResponseBody
    public ResponseEntity<ProjectResponse> showEditForm(
            @PathVariable Long projectId
    ) {
        Project project = projectService.findProjectById(projectId);
        return ResponseEntity.ok(ProjectResponse.from(project));
    }

    /** 프로젝트 수정 */
    @PostMapping("/edit/{projectId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProject(
            @PathVariable Long projectId,
            @RequestBody ProjectCreateRequest request
    ) {
        projectService.updateProject(projectId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Project Updated");
        response.put("projectId", projectId);

        return ResponseEntity.ok(response);
    }

    /** 프로젝트 삭제 */
    @PostMapping("/delete/{projectId}")
    @ResponseBody
    public ResponseEntity<String> deleteProject(
            @PathVariable Long projectId
    ) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok("{\"message\": \"Project Deleted\"}");
    }

    /** 프로젝트 보관함 */
    @GetMapping("/archive")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> showArchive(Authentication authentication) {
        String username = authentication.getName();
        Map<String, Object> response = new HashMap<>();

        // 진행 중인 프로젝트 (DTO 변환)
        List<ProjectResponse> ongoing = projectService.getOngoingProjects(username).stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
        response.put("ongoingProjects", ongoing);

        // 완료된 프로젝트 (DTO 변환)
        List<ProjectResponse> completed = projectService.getCompletedProjects(username).stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
        response.put("completedProjects", completed);

        return ResponseEntity.ok(response);
    }

    /** 중요 표시 토글 처리 */
    @PostMapping("/{projectId}/important")
    @ResponseBody
    public ResponseEntity<String> toggleImportant(@PathVariable Long projectId) {
        projectService.toggleImportant(projectId);
        return ResponseEntity.ok("{\"message\": \"Important Toggled\"}");
    }
}