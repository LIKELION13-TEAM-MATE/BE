package com.example.team_mate.domain.role.role.controller;

import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.service.ProjectService;
import com.example.team_mate.domain.role.role.entity.ProjectRole;
import com.example.team_mate.domain.role.role.entity.ProjectRoleAssignment;
import com.example.team_mate.domain.role.role.service.ProjectRoleAssignmentService;
import com.example.team_mate.domain.role.role.service.ProjectRoleService;
import com.example.team_mate.domain.team.team.entity.TeamMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project/{projectId}/roles")
public class ProjectRoleController {

    private final ProjectRoleService projectRoleService;
    private final ProjectRoleAssignmentService assignmentService;
    private final ProjectService projectService;

    /** 역할 관리 페이지 */
    @GetMapping
    public String showRolePage(@PathVariable Long projectId, Model model) {

        // 카테고리, 프로젝트 이름
        Project project = projectService.findProjectById(projectId);
        model.addAttribute("project", project);
        model.addAttribute("category", project.getCategory());
        model.addAttribute("projectName", project.getProjectName());

        // 팀원 목록 (TeamMembership 리스트)
        List<TeamMembership> teamMembers = projectRoleService.getTeamMembers(projectId);

        // 역할 키워드 목록
        List<ProjectRole> roles = projectRoleService.getRolesByProjectId(projectId);

        // 이미 매칭된 역할 정보
        List<ProjectRoleAssignment> assignments =
                assignmentService.getAssignmentsForProject(projectId);

        // memberId -> 역할 리스트 맵
        Map<Long, List<ProjectRole>> memberRolesMap = assignments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getMember().getId(),
                        Collectors.mapping(ProjectRoleAssignment::getProjectRole,
                                Collectors.toList())
                ));

        model.addAttribute("projectId", projectId);
        model.addAttribute("teamMembers", teamMembers);
        model.addAttribute("roles", roles);
        model.addAttribute("memberRolesMap", memberRolesMap);

        return "role/role";
    }

    /** 역할 키워드 추가 (직접 추가하기 버튼) */
    @PostMapping("/add")
    public String addRole(@PathVariable Long projectId,
                          @RequestParam String roleName) {

        projectRoleService.createRole(projectId, roleName);
        return "redirect:/project/" + projectId + "/roles";
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
    public ResponseEntity<Void> unassignRole(
            @RequestParam Long roleId,
            @RequestParam Long memberId
    ) {
        assignmentService.unassignRole(roleId, memberId);
        return ResponseEntity.ok().build();
    }
}
