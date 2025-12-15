package com.example.team_mate.domain.member.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class LoginResponse {

    @Schema(description = "회원 ID", example = "1")
    private Long id;

    @Schema(description = "아이디", example = "string")
    private String username;

    @Schema(description = "닉네임", example = "string")
    private String nickname;
}
