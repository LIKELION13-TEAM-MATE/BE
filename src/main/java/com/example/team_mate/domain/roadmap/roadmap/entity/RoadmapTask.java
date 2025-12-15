package com.example.team_mate.domain.roadmap.roadmap.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapTask {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content; // 할 일 내용
    private boolean isChecked; // 체크 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id")
    private Roadmap roadmap;

    private String note;

    public RoadmapTask(String content, Roadmap roadmap) {
        this.content = content;
        this.roadmap = roadmap;
        this.isChecked = false;
    }

    // 체크 상태 토글
    public void toggleCheck() {
        this.isChecked = !this.isChecked;
    }

    public void updateNote(String note) {
        this.note = note;
    }
}