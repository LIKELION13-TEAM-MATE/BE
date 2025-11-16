package com.example.team_mate.domain.member.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(unique = true, nullable = false) // 중복 방지
    private String username; // 아이디

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String nickname; // 닉네임

    // 이 멤버가 리더인 프로젝트 목록
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL) // 'member' 필드와 연결됨
    private java.util.List<com.example.team_mate.domain.project.project.entity.Project> projects = new java.util.ArrayList<>();

    // 이 멤버가 참가 기록 목록
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<com.example.team_mate.domain.team.team.entity.TeamMembership> teamMemberships = new java.util.ArrayList<>();


}