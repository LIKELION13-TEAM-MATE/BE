package com.example.team_mate.domain.role.role.repository;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.role.role.entity.ProjectRole;
import com.example.team_mate.domain.role.role.entity.ProjectRoleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRoleAssignmentRepository
        extends JpaRepository<ProjectRoleAssignment, Long> {

    // 한 프로젝트 안의 모든 배정 정보
    List<ProjectRoleAssignment> findByProjectRole_Project_Id(Long projectId);

    // 중복 배정 방지용
    boolean existsByProjectRoleAndMember(ProjectRole projectRole, Member member);

    Optional<ProjectRoleAssignment> findByProjectRoleAndMember(ProjectRole projectRole, Member member);

    // 특정 멤버의 특정 역할 해제
    void deleteByProjectRole_IdAndMember_Id(Long roleId, Long memberId);
}
