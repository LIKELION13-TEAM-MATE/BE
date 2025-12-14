package com.example.team_mate.domain.chatroom.chatroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "채팅 메시지 응답 DTO")
public record ChatMessageResponse(

        @Schema(description = "메시지 ID", example = "501")
        Long messageId,

        @Schema(description = "보낸 사람 memberId", example = "10")
        Long senderId,

        @Schema(description = "보낸 사람 닉네임", example = "김멋사")
        String senderNickname,

        @Schema(description = "메시지 내용", example = "오늘 회의 몇 시야?")
        String content,

        @Schema(description = "메시지 생성 시간", example = "2025-12-14T14:37:12")
        LocalDateTime createdAt
) {}
