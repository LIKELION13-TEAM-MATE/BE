package com.example.team_mate.domain.role.role.service;

import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.repository.ProjectRepository;
import com.example.team_mate.domain.role.role.entity.ProjectRole;
import com.example.team_mate.domain.role.role.repository.ProjectRoleRepository;
import com.example.team_mate.domain.team.team.entity.TeamMembership;
import com.example.team_mate.domain.team.team.repository.TeamMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectRoleService {

    private final ProjectRepository projectRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final TeamMembershipRepository teamMembershipRepository;

    /** 프로젝트에 속한 팀원 목록 */
    @Transactional(readOnly = true)
    public List<TeamMembership> getTeamMembers(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. id=" + projectId));
        return teamMembershipRepository.findByProject(project);
    }

    /** 프로젝트에 등록된 역할 목록 */
    @Transactional(readOnly = true)
    public List<ProjectRole> getRolesByProjectId(Long projectId) {
        return projectRoleRepository.findByProjectId(projectId);
    }

    /** 역할 키워드 생성 */
    @Transactional
    public ProjectRole createRole(Long projectId, String roleName) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. id=" + projectId));

        ProjectRole role = new ProjectRole();
        role.setProject(project);
        role.setName(roleName);

        return projectRoleRepository.save(role);
    }
}
