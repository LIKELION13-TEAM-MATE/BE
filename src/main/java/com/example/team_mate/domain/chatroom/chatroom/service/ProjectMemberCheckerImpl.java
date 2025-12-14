package com.example.team_mate.domain.chatroom.chatroom.service;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.repository.MemberRepository;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.repository.ProjectRepository;
import com.example.team_mate.domain.team.team.repository.TeamMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectMemberCheckerImpl implements ProjectMemberChecker {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final TeamMembershipRepository teamMembershipRepository;

    @Override
    public boolean isProjectMember(Long projectId, Long memberId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // 리더도 참여자 취급
        if (project.getMember() != null && project.getMember().getId().equals(memberId)) {
            return true;
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return teamMembershipRepository.existsByMemberAndProject(member, project);
    }
}
