package com.example.team_mate.domain.chatroom.chatroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 참여자 아바타 표시용 DTO")
public record MemberAvatarResponse(

        @Schema(description = "멤버 ID", example = "3")
        Long memberId,

        @Schema(description = "멤버 닉네임", example = "이멋사")
        String nickname,

        @Schema(description = "닉네임 첫 글자(프로필 원 안에 표시)", example = "이")
        String initial,

        @Schema(description = "아바타 배경색(닉네임 기반 고정 색상)", example = "#03A9F4")
        String avatarColor
) {}
