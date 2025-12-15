package com.example.team_mate.domain.member.member.dto;

import com.example.team_mate.domain.member.member.entity.Member;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "회원 정보 응답 DTO")
public class MemberResponse {

    @Schema(description = "회원 ID", example = "1")
    private Long id;

    @Schema(description = "아이디", example = "likelion1111")
    private String username;

    @Schema(description = "닉네임", example = "김멋사")
    private String nickname;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.nickname = member.getNickname();
    }
}
