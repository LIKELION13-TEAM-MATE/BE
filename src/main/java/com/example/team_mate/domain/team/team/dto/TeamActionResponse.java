package com.example.team_mate.domain.team.team.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "팀 관련 공통 응답 DTO(결과 메시지)")
public class TeamActionResponse {

    @Schema(description = "결과 메시지", example = "팀원을 성공적으로 초대했습니다!")
    private String message;
}
