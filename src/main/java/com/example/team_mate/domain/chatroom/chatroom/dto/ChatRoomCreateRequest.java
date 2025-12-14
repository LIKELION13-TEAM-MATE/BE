package com.example.team_mate.domain.chatroom.chatroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "채팅방 생성 요청 DTO")
public record ChatRoomCreateRequest(

        @Schema(description = "채팅방 이름", example = "멋사 동아리 채팅방")
        String name,

        @Schema(description = "초대할 멤버 ID 목록(프로젝트 참여자만 가능)", example = "[2, 3, 7]", nullable = true)
        List<Long> inviteMemberIds,

        @Schema(description = "채팅방 비밀번호(선택). null/\"\"이면 비번 없음, 있으면 4자리 숫자", example = "1234", nullable = true)
        String password
) {}
