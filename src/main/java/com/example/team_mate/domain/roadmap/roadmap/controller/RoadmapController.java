package com.example.team_mate.domain.roadmap.roadmap.controller;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.repository.MemberRepository;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.service.ProjectService;
import com.example.team_mate.domain.roadmap.roadmap.entity.Roadmap;
import com.example.team_mate.domain.roadmap.roadmap.service.RoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;
    private final ProjectService projectService;
    private final MemberRepository memberRepository;

    /** 로드맵 조회 */
    @GetMapping("/project/{projectId}/roadmap")
    public String showRoadmap(@PathVariable Long projectId,
                              @RequestParam(required = false) Long memberId,
                              Authentication authentication,
                              Model model) {

        Project project = projectService.findProjectById(projectId);
        Long resolvedMemberId = resolveMemberId(memberId, authentication);
        List<Roadmap> roadmaps = roadmapService.getRoadmapsByProject(projectId);
        int totalProgress = roadmapService.getTotalProgress(projectId);

        // 단계 현황 계산
        int totalCount = roadmaps.size(); // 총 단계 수
        // 진행률 100
        long completedCount = roadmaps.stream().filter(r -> r.getProgress() == 100).count();

        model.addAttribute("project", project);
        model.addAttribute("memberId", resolvedMemberId);
        model.addAttribute("roadmaps", roadmaps);
        model.addAttribute("totalProgress", totalProgress);

        model.addAttribute("totalCount", totalCount);
        model.addAttribute("completedCount", completedCount);

        return "roadmap/list";
    }

    /** 로드맵 생성 form */
    @GetMapping("/project/{projectId}/roadmap/create")
    public String showCreateForm(@PathVariable Long projectId, Model model) {
        Project project = projectService.findProjectById(projectId);
        model.addAttribute("project", project);
        return "roadmap/create";
    }

    /** 로드맵 생성 */
    @PostMapping("/project/{projectId}/roadmap/create")
    public String createRoadmap(
            @PathVariable Long projectId,
            @RequestParam String role,
            @RequestParam String title,
            @RequestParam LocalDate deadline,
            @RequestParam(required = false) List<Long> memberIds
    ) {
        roadmapService.createRoadmap(projectId, role, title, deadline, memberIds);
        return "redirect:/project/" + projectId + "/roadmap";
    }

    /** Task 추가 */
    @PostMapping("/roadmap/{roadmapId}/task/add")
    public String addTask(@PathVariable Long roadmapId, @RequestParam String content, @RequestParam Long projectId) {
        roadmapService.addTask(roadmapId, content);
        return "redirect:/project/" + projectId + "/roadmap";
    }

    /** Task 체크 토글 */
    @PostMapping("/roadmap/{taskId}/check")
    public String toggleTask(@PathVariable Long taskId, @RequestParam Long projectId) {
        roadmapService.toggleTask(taskId);
        return "redirect:/project/" + projectId + "/roadmap";
    }

    /** Task 주석(comment) 등록 */
    @PostMapping("/roadmap/task/{taskId}/note")
    public String updateTaskNote(
            @PathVariable Long taskId,
            @RequestParam String note,
            @RequestParam Long projectId
    ) {
        roadmapService.updateTaskNote(taskId, note);
        return "redirect:/project/" + projectId + "/roadmap";
    }

    /** Task 삭제 */
    @PostMapping("/roadmap/task/{taskId}/delete")
    public String deleteTask(@PathVariable Long taskId, @RequestParam Long projectId) {
        roadmapService.deleteTask(taskId);
        return "redirect:/project/" + projectId + "/roadmap";
    }

    /** 로드맵 수정 form */
    @GetMapping("/roadmap/{roadmapId}/edit")
    public String showEditForm(@PathVariable Long roadmapId, @RequestParam Long projectId, Model model) {
        Roadmap roadmap = roadmapService.getRoadmapById(roadmapId);
        Project project = projectService.findProjectById(projectId);

        model.addAttribute("roadmap", roadmap);
        model.addAttribute("project", project);

        return "roadmap/edit"; // roadmap/edit.html
    }

    /** 로드맵 수정 */
    @PostMapping("/roadmap/{roadmapId}/edit")
    public String updateRoadmap(
            @PathVariable Long roadmapId,
            @RequestParam Long projectId,
            @RequestParam String role,
            @RequestParam String title,
            @RequestParam LocalDate deadline,
            @RequestParam(required = false) List<Long> memberIds
    ) {
        roadmapService.updateRoadmap(roadmapId, role, title, deadline,memberIds);
        return "redirect:/project/" + projectId + "/roadmap";
    }

    /** 로드맵 삭제 */
    @PostMapping("/roadmap/{roadmapId}/delete")
    public String deleteRoadmap(@PathVariable Long roadmapId, @RequestParam Long projectId) {
        roadmapService.deleteRoadmap(roadmapId);
        return "redirect:/project/" + projectId + "/roadmap";
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
}