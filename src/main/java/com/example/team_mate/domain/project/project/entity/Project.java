package com.example.team_mate.domain.project.project.entity;

import com.example.team_mate.domain.member.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName; // 프로젝트 이름
    private String category;    // 카테고리
    private LocalDate deadline; // 마감일
    private String themeColor;  // 테마 색상

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 성능 최적화
    @JoinColumn(name = "leader_id")
    private Member member;

    public Project(String projectName, String category, LocalDate deadline, String themeColor, Member member) {
        this.projectName = projectName;
        this.category = category;
        this.deadline = deadline;
        this.themeColor = themeColor;
        this.member = member;
    }

    public void update(String projectName, String category, LocalDate deadline, String themeColor) {
        this.projectName = projectName;
        this.category = category;
        this.deadline = deadline;
        this.themeColor = themeColor;
    }

    // 이 프로젝트의 참가자
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<com.example.team_mate.domain.team.team.entity.TeamMembership> teamMemberships = new java.util.ArrayList<>();


}