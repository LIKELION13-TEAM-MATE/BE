package com.example.team_mate.domain.roadmap.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "로드맵 담당 멤버 요약 DTO")
public class MemberSummaryResponse {

    @Schema(description = "멤버 ID", example = "1")
    private Long memberId;

    @Schema(description = "멤버 아이디", example = "likelion1")
    private String username;

    @Schema(description = "멤버 닉네임", example = "김멋사")
    private String nickname;
}