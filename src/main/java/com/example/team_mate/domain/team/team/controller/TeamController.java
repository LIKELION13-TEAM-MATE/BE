package com.example.team_mate.domain.team.team.controller;

import com.example.team_mate.domain.team.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // 👈 1. (추가!)
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // 추가

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    /** 팀원 초대 form */
    @PostMapping("/project/detail/{projectId}/invite")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> inviteMember(
            @PathVariable Long projectId,
            @RequestParam String username
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            teamService.inviteMember(projectId, username);

            // 성공 메시지 반환
            response.put("message", "팀원을 성공적으로 초대했습니다!");
            response.put("projectId", projectId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // 실패 메시지 반환
            response.put("error", "초대 실패");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /** 팀원 강퇴 */
    @PostMapping("/project/{projectId}/kick/{membershipId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> kickMember(
            @PathVariable Long projectId,
            @PathVariable Long membershipId,
            Authentication authentication
    ) {
        // 요청한 사람의 ID 가져옴
        String requesterUsername = authentication.getName();
        Map<String, Object> response = new HashMap<>();

        try {
            // 권한 확인(리더인지)
            teamService.kickMember(projectId, membershipId, requesterUsername);

            // 성공 메시지 반환
            response.put("message", "팀원을 성공적으로 강퇴했습니다.");
            response.put("projectId", projectId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // 오류(권한 없음, 자신을 강퇴 등)
            response.put("error", "강퇴 실패");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}