package com.example.team_mate.domain.member.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberLoginRequest {

    @Schema(description = "아이디", example = "string")
    private String username;

    @Schema(description = "비밀번호", example = "string")
    private String password;
}
