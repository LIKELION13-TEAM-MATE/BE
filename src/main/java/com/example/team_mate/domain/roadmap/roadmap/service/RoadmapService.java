package com.example.team_mate.domain.roadmap.roadmap.service;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.repository.MemberRepository;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.repository.ProjectRepository;
import com.example.team_mate.domain.roadmap.roadmap.entity.Roadmap;
import com.example.team_mate.domain.roadmap.roadmap.entity.RoadmapMember;
import com.example.team_mate.domain.roadmap.roadmap.entity.RoadmapTask;
import com.example.team_mate.domain.roadmap.roadmap.repository.RoadmapMemberRepository;
import com.example.team_mate.domain.roadmap.roadmap.repository.RoadmapRepository;
import com.example.team_mate.domain.roadmap.roadmap.repository.RoadmapTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapTaskRepository roadmapTaskRepository;
    private final RoadmapMemberRepository roadmapMemberRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    /** 로드맵 생성 */
    @Transactional
    public void createRoadmap(Long projectId, String role, String title, LocalDate deadline, List<Long> memberIds) {
        Project project = projectRepository.findById(projectId).orElseThrow();

        Roadmap roadmap = new Roadmap(role, title, deadline, project);
        roadmapRepository.save(roadmap);

        // 동업자 추가
        if (memberIds != null) {
            for (Long memberId : memberIds) {
                Member member = memberRepository.findById(memberId).orElseThrow();
                roadmapMemberRepository.save(new RoadmapMember(roadmap, member));
            }
        }
    }

    /** Task 추가 */
    @Transactional
    public void addTask(Long roadmapId, String content) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId).orElseThrow();
        RoadmapTask task = new RoadmapTask(content, roadmap);
        roadmapTaskRepository.save(task);

        // 진행률 재계산
        roadmap.updateProgress();
    }

    /** Task 체크 토글 */
    @Transactional
    public void toggleTask(Long taskId) {
        RoadmapTask task = roadmapTaskRepository.findById(taskId).orElseThrow();
        task.toggleCheck();
        task.getRoadmap().updateProgress(); // 진행률 업데이트
    }

    /** 로드맵 목록 조회 */
    @Transactional(readOnly = true)
    public List<Roadmap> getRoadmapsByProject(Long projectId) {
        List<Roadmap> roadmaps = roadmapRepository.findAllByProjectIdOrderByIdAsc(projectId);

        // 오늘 날짜 (마감일 비교용)
        LocalDate today = LocalDate.now();

        roadmaps.sort((r1, r2) -> {
            // 진행률이 100% 이거나 OR 마감일이 오늘보다 이전인 경우
            boolean r1IsDoneOrOverdue = (r1.getProgress() == 100) || r1.getDeadline().isBefore(today);
            boolean r2IsDoneOrOverdue = (r2.getProgress() == 100) || r2.getDeadline().isBefore(today);

            if (r1IsDoneOrOverdue != r2IsDoneOrOverdue) {
                return r1IsDoneOrOverdue ? 1 : -1;
            }

            try {
                int step1 = Integer.parseInt(r1.getRole().replace("단계", ""));
                int step2 = Integer.parseInt(r2.getRole().replace("단계", ""));
                return step1 - step2;
            } catch (NumberFormatException e) {
                return 0;
            }
        });

        return roadmaps;
    }

    /** 프로젝트 전체 진행률 계산 (평균) */
    @Transactional(readOnly = true)
    public int getTotalProgress(Long projectId) {
        List<Roadmap> roadmaps = getRoadmapsByProject(projectId);
        if (roadmaps.isEmpty()) return 0;

        double total = roadmaps.stream().mapToInt(Roadmap::getProgress).sum();
        return (int) (total / roadmaps.size());
    }

    /** Task 삭제 */
    @Transactional
    public void deleteTask(Long taskId) {
        RoadmapTask task = roadmapTaskRepository.findById(taskId).orElseThrow();
        Roadmap roadmap = task.getRoadmap();

        roadmapTaskRepository.delete(task);

        // 진행률 재계산
        roadmap.getTasks().remove(task);
        roadmap.updateProgress();
    }

    /** Task 주석(comment) 등록 */
    @Transactional
    public void updateTaskNote(Long taskId, String note) {
        RoadmapTask task = roadmapTaskRepository.findById(taskId).orElseThrow();
        task.updateNote(note);
    }

    // 로드맵 단건 조회
    @Transactional(readOnly = true)
    public Roadmap getRoadmapById(Long roadmapId) {
        return roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new IllegalArgumentException("해당 로드맵이 없습니다."));
    }

    /** 로드맵 수정 */
    @Transactional
    public void updateRoadmap(Long roadmapId, String role, String title, LocalDate deadline, List<Long> memberIds) {
        Roadmap roadmap = getRoadmapById(roadmapId);

        roadmap.update(role, title, deadline);

        if (memberIds != null) {
            roadmap.getMembers().clear();

            for (Long memberId : memberIds) {
                Member member = memberRepository.findById(memberId).orElseThrow();
                roadmapMemberRepository.save(new RoadmapMember(roadmap, member));
            }
        }
    }

    /** 로드맵 삭제 */
    @Transactional
    public void deleteRoadmap(Long roadmapId) {
        Roadmap roadmap = getRoadmapById(roadmapId);
        roadmapRepository.delete(roadmap);
    }

}