package com.example.team_mate.domain.team.team.service;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.repository.MemberRepository;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.repository.ProjectRepository;
import com.example.team_mate.domain.team.team.dto.TeamMemberResponse;
import com.example.team_mate.domain.team.team.entity.TeamMembership;
import com.example.team_mate.domain.team.team.repository.TeamMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamMembershipRepository teamMembershipRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    /** 팀원 초대 */
    @Transactional
    public void inviteMember(Long projectId, String usernameToInvite) {

        // 없는 유저 체크
        Member memberToInvite = memberRepository.findByUsername(usernameToInvite)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 프로젝트 찾기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        // 중복 유저 체크
        //    방금 Repository에 만든 메서드를 사용합니다.
        boolean alreadyExists = teamMembershipRepository.existsByMemberAndProject(memberToInvite, project);

        if (alreadyExists) {
            // 중복된 유저면 메시지 띄우기
            throw new IllegalArgumentException("이미 초대된 유저입니다.");
        }

        // 참가자 명단 생성
        TeamMembership newMembership = new TeamMembership(memberToInvite, project);

        // 참가자 명단 저장
        teamMembershipRepository.save(newMembership);
    }

    /** 팀원 강퇴 */
    @Transactional
    public void kickMember(Long projectId, Long membershipId, String requesterUsername) {

        // 프로젝트 실재하는지 확인
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // 프로젝트의 리더 맞는지 확인
        if (!project.getMember().getUsername().equals(requesterUsername)) {
            throw new IllegalArgumentException("팀원 삭제 권한이 없습니다. (리더 아님)");
        }

        // 강퇴할 멤버 ID로 찾기
        TeamMembership membershipToKick = teamMembershipRepository.findById(membershipId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀원 참가 기록을 찾을 수 없습니다."));

        // 리더가 자기 자신 강퇴하는지 확인
        if (membershipToKick.getMember().getId().equals(project.getMember().getId())) {
            throw new IllegalArgumentException("리더는 자신을 강퇴할 수 없습니다.");
        }

        // 팀원 DB에서 삭제
        teamMembershipRepository.delete(membershipToKick);
    }

    /** 팀원 초대 목록 */
    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getTeamMembers(Long projectId) {
        List<TeamMembership> memberships = teamMembershipRepository.findByProjectId(projectId);

        return memberships.stream()
                .map(TeamMembership::getMember) // 멤버십에서 Member 엔티티 추출
                .filter(member -> member != null) // 혹시 모를 null 방지
                .map(TeamMemberResponse::new) // Member -> TeamMemberResponse 변환 (이니셜/색상 포함)
                .toList();
    }
}