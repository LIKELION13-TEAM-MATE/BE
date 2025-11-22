package com.example.team_mate.domain.member.member.dto;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class MemberLoginRequest {

    @Schema(description = "아이디", example = "string")
    private String username;

    @Schema(description = "비밀번호", example = "string")
    private String password;
}
