package com.example.team_mate.domain.team.team.dto;

import com.example.team_mate.domain.member.member.entity.Member;
import lombok.Getter;

@Getter
public class TeamMemberResponse {
    private Long id;
    private String nickname;
    private String username;
    private String initial;      // 닉네임 첫 글자
    private String avatarColor;  // 프로필 배경색

    public TeamMemberResponse(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.username = member.getUsername();
        this.initial = member.getInitial();
        this.avatarColor = member.getAvatarColor();
    }
}