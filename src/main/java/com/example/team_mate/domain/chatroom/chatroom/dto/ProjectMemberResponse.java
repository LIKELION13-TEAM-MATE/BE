package com.example.team_mate.domain.chatroom.chatroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 참여자(채팅방 초대용) DTO")
public record ProjectMemberResponse(

        @Schema(description = "멤버 ID", example = "3")
        Long memberId,

        @Schema(description = "멤버 닉네임", example = "이멋사")
        String nickname
) {}
