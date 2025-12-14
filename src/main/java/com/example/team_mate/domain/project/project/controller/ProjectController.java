package com.example.team_mate.domain.project.project.controller;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.repository.MemberRepository;
import com.example.team_mate.domain.post.post.service.PostService;
import com.example.team_mate.domain.project.project.dto.ProjectCreateRequest;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final PostService postService;
    private final MemberRepository memberRepository;

    /** 새 프로젝트 생성 form */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("request", new ProjectCreateRequest());
        return "project/create";
    }

    /** 프로젝트 생성 */
    @PostMapping("/create")
    public String createProject(ProjectCreateRequest request, Authentication authentication) {
        String username = authentication.getName();
        Project newProject = projectService.createProject(request, username);

        return "redirect:/project/detail/" + newProject.getId();
    }

    /** 프로젝트 detail */
    @GetMapping("/detail/{projectId}")
    public String showProjectDetail(
            @PathVariable Long projectId,
            Model model,
            Authentication authentication
    ) {
        // 프로젝트 정보 가져오기
        Project project = projectService.findProjectById(projectId);
        model.addAttribute("project", project);

        // 게시글 목록 가져오기
        java.util.List<com.example.team_mate.domain.post.post.entity.Post> posts = postService.getPostsByProject(projectId);
        model.addAttribute("posts", posts);

        // 로그인 사용자 memberId
        String username = authentication.getName();
        Member me = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Member not found: " + username));
        model.addAttribute("memberId", me.getId());

        return "project/detail";
    }

    /** 프로젝트 수정 form */
    @GetMapping("/edit/{projectId}")
    public String showEditForm(
            @PathVariable Long projectId,
            Model model
    ) {
        Project project = projectService.findProjectById(projectId);
        model.addAttribute("project", project);
        return "project/edit";
    }

    /** 프로젝트 수정 */
    @PostMapping("/edit/{projectId}")
    public String updateProject(
            @PathVariable Long projectId,
            ProjectCreateRequest request
    ) {
        projectService.updateProject(projectId, request);
        return "redirect:/project/detail/" + projectId;
    }

    /** 프로젝트 삭제 */
    @PostMapping("/delete/{projectId}")
    public String deleteProject(
            @PathVariable Long projectId
    ) {
        projectService.deleteProject(projectId);
        return "redirect:/project/archive";
    }

    /** 프로젝트 보관함 */
    @GetMapping("/archive")
    public String showArchive(Model model, Authentication authentication) {
        String username = authentication.getName();

        // 진행 중인 프로젝트
        model.addAttribute("ongoingProjects", projectService.getOngoingProjects(username));
        // 완료된 프로젝트
        model.addAttribute("completedProjects", projectService.getCompletedProjects(username));

        return "project/archive"; // project/archive.html
    }

    /** 중요 표시 토글 처리 */
    @PostMapping("/{projectId}/important")
    public String toggleImportant(@PathVariable Long projectId) {
        projectService.toggleImportant(projectId);
        return "redirect:/project/archive";
    }
}