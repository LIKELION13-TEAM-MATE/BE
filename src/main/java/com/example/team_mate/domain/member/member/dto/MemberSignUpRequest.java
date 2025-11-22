package com.example.team_mate.domain.member.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
public class MemberSignUpRequest {

    @Schema(description = "아이디", example = "string")
    private String username;

    @Schema(description = "비밀번호", example = "stirng")
    private String password;

    @Schema(description = "닉네임", example = "string")
    private String nickname;
}
