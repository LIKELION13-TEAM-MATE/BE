package com.example.team_mate.domain.chatroom.chatroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "프로젝트 내 채팅방 목록 조회 응답 DTO")
public record ChatRoomResponse(

        @Schema(description = "채팅방 ID", example = "12")
        Long chatRoomId,

        @Schema(description = "채팅방 이름", example = "멋사 동아리 채팅방")
        String name,

        @Schema(description = "비밀번호 설정 여부", example = "false")
        boolean passwordEnabled,

        @Schema(description = "채팅방 생성 시각", example = "2025-12-14T14:30:00")
        LocalDateTime createdAt,

        @Schema(description = "채팅방 참여 인원 수", example = "5")
        long memberCount,

        @Schema(description = "채팅방 대표 아바타 목록(최대 4명 정도 표시용)")
        List<MemberAvatarResponse> avatars,

        @Schema(description = "채팅방 최근 메시지(없으면 빈 문자열)", example = "오늘 회의 몇 시에 할까?")
        String lastMessage,

        @Schema(description = "최근 메시지 표시용 시간/날짜 (오늘이면 시간, 아니면 'M월 d일')", example = "오후 2:10")
        String lastDisplayTime
) {}
