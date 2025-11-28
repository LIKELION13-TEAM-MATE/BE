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

    /** 중요 표시 토글 */
    @Transactional
    public void toggleImportant(Long projectId) {
        Project project = findProjectById(projectId);
        project.toggleImportant();
    }

    /** 진행 중인 프로젝트 목록 조회(메인홈용) */
    @Transactional(readOnly = true)
    public List<Project> getOngoingProjects(String username) {
        List<Project> allMyProjects = findMyProjects(username);

        return allMyProjects.stream()
                .filter(p -> !p.isCompleted()) // 완료되지 않은 것만 필터링
                .sorted((p1, p2) -> {
                    // 중요 체크
                    if (p1.isImportant() != p2.isImportant()) {
                        return p1.isImportant() ? -1 : 1;
                    }
                    // 마감일 비교: 마감일 빠른 게 앞으로
                    if (p1.getDeadline() != null && p2.getDeadline() != null) {
                        return p1.getDeadline().compareTo(p2.getDeadline());
                    }
                    return 0;
                })
                .collect(Collectors.toList());
    }

    /** 완료된 프로젝트 목록 조회(보관함용) */
    @Transactional(readOnly = true)
    public List<Project> getCompletedProjects(String username) {
        List<Project> allMyProjects = findMyProjects(username);

        return allMyProjects.stream()
                .filter(Project::isCompleted) // 완료된 것만 필터링
                .collect(Collectors.toList());
    }

}