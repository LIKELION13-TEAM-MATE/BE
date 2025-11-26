package com.example.team_mate.domain.team.team.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "프로젝트 팀원 초대 요청 DTO")
public class TeamInviteRequest {

    @Schema(
            description = "초대할 사용자 아이디(로그인 ID)",
            example = "likelion1111",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;
}
