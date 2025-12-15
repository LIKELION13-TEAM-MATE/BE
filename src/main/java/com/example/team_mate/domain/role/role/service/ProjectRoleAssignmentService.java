package com.example.team_mate.domain.role.role.service;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.repository.MemberRepository;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.repository.ProjectRepository;
import com.example.team_mate.domain.role.role.entity.ProjectRole;
import com.example.team_mate.domain.role.role.entity.ProjectRoleAssignment;
import com.example.team_mate.domain.role.role.repository.ProjectRoleAssignmentRepository;
import com.example.team_mate.domain.role.role.repository.ProjectRoleRepository;
import com.example.team_mate.domain.team.team.entity.TeamMembership;
import com.example.team_mate.domain.team.team.repository.TeamMembershipRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectRoleAssignmentService {

    private final ProjectRoleAssignmentRepository assignmentRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final TeamMembershipRepository teamMembershipRepository;

    /**
     * 프로젝트에 속한 모든 역할 배정 정보 조회
     */
    public List<ProjectRoleAssignment> getAssignmentsForProject(Long projectId) {
        return assignmentRepository.findByProjectRole_Project_Id(projectId);
    }

    /**
     * 역할을 멤버에게 배정
     */
    public ProjectRoleAssignment assignRole(Long projectId, Long roleId, Long memberId) {

        ProjectRole role = projectRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("역할을 찾을 수 없습니다."));

        // 역할이 해당 프로젝트의 역할인지 체크
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        if (!role.getProject().getId().equals(project.getId())) {
            throw new IllegalArgumentException("이 역할은 해당 프로젝트에 속해 있지 않습니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));

        // 팀원인지 검증 (아니면 배정 불가)
        boolean isTeamMember = teamMembershipRepository
                .findByProject(project)
                .stream()
                .map(TeamMembership::getMember)
                .anyMatch(m -> m.getId().equals(member.getId()));

        if (!isTeamMember) {
            throw new IllegalArgumentException("이 프로젝트의 팀원이 아닌 사용자입니다.");
        }

        // 이미 같은 역할이 배정되어 있다면 그대로 리턴
        if (assignmentRepository.existsByProjectRoleAndMember(role, member)) {
            return assignmentRepository
                    .findByProjectRoleAndMember(role, member)
                    .orElseThrow();
        }

        ProjectRoleAssignment assignment = ProjectRoleAssignment.builder()
                .projectRole(role)
                .member(member)
                .build();

        return assignmentRepository.save(assignment);
    }

    /**
     * 멤버에게 배정된 역할 해제
     */
    public void unassignRole(Long roleId, Long memberId) {
        assignmentRepository.deleteByProjectRole_IdAndMember_Id(roleId, memberId);
    }
}
