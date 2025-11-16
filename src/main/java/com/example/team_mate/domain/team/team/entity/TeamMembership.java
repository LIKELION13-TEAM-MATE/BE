package com.example.team_mate.domain.team.team.entity;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.project.project.entity.Project;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 참가 기록 ID

    // 어떤 멤버를 초대
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 어떤 프로젝트에 초대할 것인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public TeamMembership(Member member, Project project) {
        this.member = member;
        this.project = project;
    }
}