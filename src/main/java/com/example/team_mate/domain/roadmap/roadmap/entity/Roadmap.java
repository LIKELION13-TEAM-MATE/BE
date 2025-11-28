package com.example.team_mate.domain.roadmap.roadmap.entity;

import com.example.team_mate.domain.project.project.entity.Project;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Roadmap {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;    // 단계 설정
    private String title;   // 제목
    private LocalDate deadline; // 마감일
    private int progress;   // 진행률 (0 ~ 100%)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    // 세부 Task 목록
    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoadmapTask> tasks = new ArrayList<>();

    // 배정된 동업자 목록
    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoadmapMember> members = new ArrayList<>();

    public Roadmap(String role, String title, LocalDate deadline, Project project) {
        this.role = role;
        this.title = title;
        this.deadline = deadline;
        this.project = project;
        this.progress = 0; // 초기 진행률 0
    }

    // 진행률 업데이트 로직
    public void updateProgress() {
        if (tasks.isEmpty()) {
            this.progress = 0;
            return;
        }
        long completedCount = tasks.stream().filter(RoadmapTask::isChecked).count();
        this.progress = (int) ((double) completedCount / tasks.size() * 100);
    }

    // 로드맵 수정
    public void update(String role, String title, LocalDate deadline) {
        this.role = role;
        this.title = title;
        this.deadline = deadline;
    }

}
