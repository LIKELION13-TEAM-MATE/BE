package com.example.team_mate.domain.role.role.controller;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.repository.MemberRepository;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.service.ProjectService;
import com.example.team_mate.domain.role.role.entity.ProjectRole;
import com.example.team_mate.domain.role.role.entity.ProjectRoleAssignment;
import com.example.team_mate.domain.role.role.service.ProjectRoleAssignmentService;
import com.example.team_mate.domain.role.role.service.ProjectRoleService;
import com.example.team_mate.domain.team.team.entity.TeamMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project/{projectId}/roles")
public class ProjectRoleController {

    private final ProjectRoleService projectRoleService;
    private final ProjectRoleAssignmentService assignmentService;
    private final ProjectService projectService;
    private final MemberRepository memberRepository;

    /** 역할 관리 페이지 */
    @GetMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> showRolePage(@PathVariable Long projectId,
                                                            @RequestParam(required = false) Long memberId,
                                                            Authentication authentication) {

        // 응답을 담을 Map 생성
        Map<String, Object> response = new HashMap<>();

        // 카테고리, 프로젝트 이름
        Project project = projectService.findProjectById(projectId);

        // [수정] Project 엔티티 전체 대신 필요한 정보만 Map에 담기 (순환 참조 방지)
        Map<String, Object> projectInfo = new HashMap<>();
        projectInfo.put("id", project.getId());
        projectInfo.put("projectName", project.getProjectName());
        projectInfo.put("category", project.getCategory());
        response.put("project", projectInfo);

        Long resolvedMemberId = memberId;
        if (resolvedMemberId == null && authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Member me = memberRepository.findByUsername(username)
                    .orElseThrow(() -> new NoSuchElementException("Member not found: " + username));
            resolvedMemberId = me.getId();
        }
        response.put("memberId", resolvedMemberId);

        // 팀원 목록 (TeamMembership 리스트)
        List<TeamMembership> teamMembers = projectRoleService.getTeamMembers(projectId);

        // [수정] TeamMembership -> 단순화된 Map List로 변환
        List<Map<String, Object>> teamMemberList = teamMembers.stream().map(tm -> {
            Map<String, Object> m = new HashMap<>();
            m.put("memberId", tm.getMember().getId());
            m.put("username", tm.getMember().getUsername());
            m.put("nickname", tm.getMember().getNickname());
            return m;
        }).collect(Collectors.toList());
        response.put("teamMembers", teamMemberList);

        // 역할 키워드 목록
        List<ProjectRole> roles = projectRoleService.getRolesByProjectId(projectId);

        // [수정] ProjectRole -> 단순화된 Map List로 변환
        List<Map<String, Object>> roleList = roles.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", r.getId());
            m.put("name", r.getName());
            return m;
        }).collect(Collectors.toList());
        response.put("roles", roleList);

        // 이미 매칭된 역할 정보
        List<ProjectRoleAssignment> assignments =
                assignmentService.getAssignmentsForProject(projectId);

        // memberId -> 역할 리스트 맵
        // [수정] ProjectRole 객체 대신 Map 리스트로 변환하여 저장
        Map<Long, List<Map<String, Object>>> memberRolesMap = new HashMap<>();

        for (ProjectRoleAssignment assignment : assignments) {
            Long mId = assignment.getMember().getId();
            memberRolesMap.putIfAbsent(mId, new ArrayList<>());

            Map<String, Object> roleMap = new HashMap<>();
            roleMap.put("id", assignment.getProjectRole().getId());
            roleMap.put("name", assignment.getProjectRole().getName());
            roleMap.put("assignmentId", assignment.getId()); // 할당 ID도 필요할 수 있음

            memberRolesMap.get(mId).add(roleMap);
        }
        response.put("memberRolesMap", memberRolesMap);

        return ResponseEntity.ok(response);
    }

    /** 역할 키워드 추가 (직접 추가하기 버튼) */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addRole(@PathVariable Long projectId,
                                                       @RequestParam(required = false) Long memberId,
                                                       @RequestParam String roleName) {

        ProjectRole role = projectRoleService.createRole(projectId, roleName);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Role Created");
        response.put("roleId", role.getId());
        response.put("roleName", role.getName());
        response.put("projectId", projectId);

        if (memberId != null) {
            response.put("memberId", memberId);
        }

        return ResponseEntity.ok(response);
    }

    /** Ajax: 특정 멤버에게 역할 배정 (드래그-드롭 시 호출) */
    @PostMapping("/assign")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> assignRole(
            @PathVariable Long projectId,
            @RequestParam Long roleId,
            @RequestParam Long memberId
    ) {
        ProjectRoleAssignment assignment =
                assignmentService.assignRole(projectId, roleId, memberId);

        Map<String, Object> body = new HashMap<>();
        body.put("assignmentId", assignment.getId());
        body.put("roleId", assignment.getProjectRole().getId());
        body.put("roleName", assignment.getProjectRole().getName());
        body.put("memberId", assignment.getMember().getId());

        return ResponseEntity.ok(body);
    }

    /** Ajax: 특정 멤버에게서 역할 해제 */
    @PostMapping("/unassign")
    @ResponseBody
    public ResponseEntity<String> unassignRole( // Void -> String 변경
                                                @RequestParam Long roleId,
                                                @RequestParam Long memberId
    ) {
        assignmentService.unassignRole(roleId, memberId);
        return ResponseEntity.ok("{\"message\": \"Role Unassigned\"}");
    }
}