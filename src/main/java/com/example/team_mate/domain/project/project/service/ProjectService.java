package com.example.team_mate.domain.project.project.service;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.repository.MemberRepository;
import com.example.team_mate.domain.project.project.dto.ProjectCreateRequest;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.repository.ProjectRepository;
import com.example.team_mate.domain.team.team.entity.TeamMembership;
import com.example.team_mate.domain.team.team.repository.TeamMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final TeamMembershipRepository teamMembershipRepository;

    /** 새 프로젝트 생성 */
    @Transactional
    public Project createProject(ProjectCreateRequest request, String username) {
        // 리더(멤버) 찾기
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 새 프로젝트 설계
        Project newProject = new Project(
                request.getProjectName(),
                request.getCategory(),
                request.getDeadline(),
                request.getThemeColor(),
                member // 리더로 설정
        );

        // 프로젝트를 먼저 저장해서 ID를 확정
        Project savedProject = projectRepository.save(newProject);

        // 리더도 팀원으로써 참가 기록 만듦
        TeamMembership leaderMembership = new TeamMembership(member, savedProject);
        teamMembershipRepository.save(leaderMembership); // '참가자' 냉장고에도 저장

        return savedProject;
    }

    /** 내 프로젝트 목록 */
    @Transactional(readOnly = true)
    public List<Project> findMyProjects(String username) {

        // 로그인한 유저를 DB에서 찾음
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));

        List<TeamMembership> myMemberships = member.getTeamMemberships();

        return myMemberships.stream()
                .map(TeamMembership::getProject)
                .collect(Collectors.toList());
    }

    /** 프로젝트 detail */
    @Transactional(readOnly = true)
    public Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 프로젝트가 없습니다. id=" + projectId));
    }

    /** 프로젝트 수정 */
    @Transactional
    public void updateProject(Long projectId, ProjectCreateRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 프로젝트가 없습니다. id=" + projectId));

        project.update(
                request.getProjectName(),
                request.getCategory(),
                request.getDeadline(),
                request.getThemeColor()
        );
    }

    /** 프로젝트 삭제 */
    @Transactional
    public void deleteProject(Long projectId) {
        projectRepository.deleteById(projectId);
    }
}