package com.example.team_mate.domain.home.home.controller;

import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProjectService projectService;

    @GetMapping("/")
    public String home() {
        return "home/first"; // 로그인 or 회원가입 화면
    }

    @GetMapping("/home/main")
    public String mainPage(Model model, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();

            // 진행 중인 프로젝트 전체 가져오기
            List<Project> ongoingProjects = projectService.getOngoingProjects(username);

            model.addAttribute("ongoingCount", ongoingProjects.size());

            model.addAttribute("mainProjects", ongoingProjects.subList(0, Math.min(ongoingProjects.size(), 2)));
        }
        return "home/main";
    }
}

