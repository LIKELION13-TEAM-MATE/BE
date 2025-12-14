package com.example.team_mate.domain.chatroom.chatroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅 메시지 전송 요청 DTO")
public record ChatMessageSendRequest(

        @Schema(description = "메시지 내용", example = "지금 어디야?")
        String content,

        @Schema(description = "채팅방 비밀번호(비밀번호가 있는 방이면 필요). 4자리 숫자", example = "1234", nullable = true)
        String roomPassword
) {}
