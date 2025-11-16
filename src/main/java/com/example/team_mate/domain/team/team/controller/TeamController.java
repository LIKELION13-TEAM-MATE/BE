package com.example.team_mate.domain.team.team.controller;

import com.example.team_mate.domain.team.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication; // 👈 1. (추가!)
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    /*****
     팀원 초대 form
     *****/
    @PostMapping("/project/detail/{projectId}/invite")
    public String inviteMember(
            @PathVariable Long projectId,
            @RequestParam String username,
            RedirectAttributes redirectAttributes
    ) {
        try {
            teamService.inviteMember(projectId, username);
            redirectAttributes.addFlashAttribute("successMessage", "팀원을 성공적으로 초대했습니다!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/project/detail/" + projectId;
    }

    /*****
     팀원 강퇴
     *****/
    @PostMapping("/project/{projectId}/kick/{membershipId}")
    public String kickMember(
            @PathVariable Long projectId,
            @PathVariable Long membershipId,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        // 요청한 사람의 ID 가져옴
        String requesterUsername = authentication.getName();

        try {
            // 권한 확인(리더인지)
            teamService.kickMember(projectId, membershipId, requesterUsername);
            redirectAttributes.addFlashAttribute("successMessage", "팀원을 성공적으로 강퇴했습니다.");

        } catch (IllegalArgumentException e) {
            // 오류(권한 없음, 자신을 강퇴 등)
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/project/detail/" + projectId;
    }
}