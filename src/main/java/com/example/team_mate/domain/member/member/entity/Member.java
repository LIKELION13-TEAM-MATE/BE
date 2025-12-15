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

    public Member(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    // 닉네임의 첫 글자 가져옴
    public String getInitial() {
        if (this.nickname != null && !this.nickname.isEmpty()) {
            return String.valueOf(this.nickname.charAt(0)).toUpperCase();
        }
        return ""; // 닉네임이 없으면 빈 문자열 반환
    }

    // 닉네임을 기반으로 고유한 배경색을 생성하는 메서드 (항상 동일한 색상 반환)
    public String getAvatarColor() {
        if (this.nickname == null || this.nickname.isEmpty()) {
            return "#CCCCCC"; // 기본 회색
        }
        int hash = this.nickname.hashCode();
        String[] colors = {
                "#FFC107", "#03A9F4", "#4CAF50", "#FF5722", "#9C27B0",
                "#00BCD4", "#FFEB3B", "#8BC34A", "#E91E63", "#673AB7"
        };
        return colors[Math.abs(hash % colors.length)];
    }

}