package com.example.team_mate.domain.team.team.repository;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.team.team.entity.TeamMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {

    // 중복 초대 방지
    boolean existsByMemberAndProject(Member member, Project project);
    List<TeamMembership> findByProject(Project project);

}