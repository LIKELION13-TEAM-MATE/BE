package com.example.team_mate.domain.chatroom.chatroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 페이지 상단 헤더 응답 DTO")
public record ChatRoomPageHeaderResponse(

        @Schema(description = "프로젝트 이름", example = "Team-Mate")
        String projectName,

        @Schema(description = "채팅방 이름", example = "멋사 동아리 채팅방")
        String chatRoomName,

        @Schema(description = "채팅방 참여 인원 수", example = "5")
        long memberCount
) {}
