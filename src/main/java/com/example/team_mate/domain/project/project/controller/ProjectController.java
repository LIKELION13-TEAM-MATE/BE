package com.example.team_mate.domain.project.project.controller;

import com.example.team_mate.domain.project.project.dto.ProjectCreateRequest;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    /*****
     새 프로젝트 생성 form
     *****/
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("request", new ProjectCreateRequest());
        return "project/create";
    }

    /*****
     프로젝트 생성
     *****/
    @PostMapping("/create")
    public String createProject(ProjectCreateRequest request, Authentication authentication) {
        String username = authentication.getName();
        Project newProject = projectService.createProject(request, username);

        return "redirect:/project/detail/" + newProject.getId();
    }

    /*****
     프로젝트 목록
     *****/
    @GetMapping("/list")
    public String showMyProjectList(Model model, Authentication authentication) {

        String username = authentication.getName();

        List<Project> myProjects = projectService.findMyProjects(username);

        model.addAttribute("projects", myProjects);
        return "project/list";
    }

    /*****
     프로젝트 detail
     *****/
    @GetMapping("/detail/{projectId}")
    public String showProjectDetail(
            @PathVariable Long projectId,
            Model model
    ) {
        Project project = projectService.findProjectById(projectId);

        model.addAttribute("project", project);

        return "project/detail";
    }

    /*****
     프로젝트 수정 form
     *****/
    @GetMapping("/edit/{projectId}")
    public String showEditForm(
            @PathVariable Long projectId,
            Model model
    ) {
        Project project = projectService.findProjectById(projectId);
        model.addAttribute("project", project);
        return "project/edit";
    }

    /*****
     프로젝트 수정
     *****/
    @PostMapping("/edit/{projectId}")
    public String updateProject(
            @PathVariable Long projectId,
            ProjectCreateRequest request
    ) {
        projectService.updateProject(projectId, request);
        return "redirect:/project/detail/" + projectId;
    }

    /*****
     프로젝트 삭제
     *****/
    @PostMapping("/delete/{projectId}")
    public String deleteProject(
            @PathVariable Long projectId
    ) {
        projectService.deleteProject(projectId);
        return "redirect:/project/list";
    }
}