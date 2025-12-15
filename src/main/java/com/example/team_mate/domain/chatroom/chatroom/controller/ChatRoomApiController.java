package com.example.team_mate.domain.chatroom.chatroom.controller;

import com.example.team_mate.domain.chatroom.chatroom.dto.*;
import com.example.team_mate.domain.chatroom.chatroom.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ChatRoom", description = "프로젝트 채팅방/메시지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatRoomApiController {

    private final ChatRoomService chatRoomService;

    @Operation(
            summary = "프로젝트 참여자 목록 조회(채팅방 초대용)",
            description = "프로젝트 참여자(리더 + 팀멤버)를 조회합니다. requesterMemberId는 요청자(본인)입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectMemberResponse.class)))),
            @ApiResponse(responseCode = "400", description = "요청값 오류/프로젝트 참여자 아님", content = @Content)
    })
    @GetMapping("/projects/{projectId}/chat-members")
    public List<ProjectMemberResponse> getProjectMembers(
            @Parameter(description = "프로젝트 ID", example = "1", required = true)
            @PathVariable Long projectId,

            @Parameter(description = "요청자(본인) memberId", example = "10", required = true)
            @RequestParam Long requesterMemberId
    ) {
        return chatRoomService.getProjectMembers(projectId, requesterMemberId);
    }

    @Operation(
            summary = "프로젝트 채팅방 목록 조회",
            description = "프로젝트 내 채팅방 목록을 조회합니다. memberId는 현재 로그인 사용자(본인)입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatRoomResponse.class)))),
            @ApiResponse(responseCode = "400", description = "요청값 오류/프로젝트 참여자 아님", content = @Content)
    })
    @GetMapping("/projects/{projectId}/chatrooms")
    public List<ChatRoomResponse> getChatRooms(
            @Parameter(description = "프로젝트 ID", example = "1", required = true)
            @PathVariable Long projectId,

            @Parameter(description = "조회 요청자(본인) memberId", example = "10", required = true)
            @RequestParam Long memberId
    ) {
        return chatRoomService.getChatRooms(projectId, memberId);
    }

    @Operation(
            summary = "채팅방 생성",
            description = "프로젝트 내 채팅방을 생성합니다. creatorMemberId는 생성자(본인)입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공 (생성된 chatRoomId 반환)",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400", description = "요청값 오류/프로젝트 참여자 아님/비밀번호 형식 오류", content = @Content)
    })
    @PostMapping("/projects/{projectId}/chatrooms")
    public Long createChatRoom(
            @Parameter(description = "프로젝트 ID", example = "1", required = true)
            @PathVariable Long projectId,

            @Parameter(description = "생성자(본인) memberId", example = "10", required = true)
            @RequestParam Long creatorMemberId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "채팅방 생성 요청 바디",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChatRoomCreateRequest.class))
            )
            @RequestBody ChatRoomCreateRequest req
    ) {
        return chatRoomService.createChatRoom(projectId, creatorMemberId, req);
    }

    @Operation(
            summary = "메시지 전송",
            description = "채팅방에 메시지를 전송합니다. 비밀번호가 설정되어 있는 방이면 roomPassword가 필요합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전송 성공 (생성된 messageId 반환)",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400", description = "요청값 오류/초대되지 않음/비밀번호 불일치", content = @Content)
    })
    @PostMapping("/chatrooms/{chatRoomId}/messages")
    public Long sendMessage(
            @Parameter(description = "채팅방 ID", example = "100", required = true)
            @PathVariable Long chatRoomId,

            @Parameter(description = "보내는 사람(본인) memberId", example = "10", required = true)
            @RequestParam Long senderMemberId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "메시지 전송 요청 바디",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChatMessageSendRequest.class))
            )
            @RequestBody ChatMessageSendRequest req
    ) {
        return chatRoomService.sendMessage(chatRoomId, senderMemberId, req);
    }

    @Operation(
            summary = "최근 메시지 조회(최대 50개)",
            description = "채팅방 최근 메시지(최대 50개)를 조회합니다. 비밀번호 방이면 roomPassword가 필요합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatMessageResponse.class)))),
            @ApiResponse(responseCode = "400", description = "요청값 오류/초대되지 않음/비밀번호 불일치", content = @Content)
    })
    @GetMapping("/chatrooms/{chatRoomId}/messages")
    public List<ChatMessageResponse> getRecentMessages(
            @Parameter(description = "채팅방 ID", example = "100", required = true)
            @PathVariable Long chatRoomId,

            @Parameter(description = "조회 요청자(본인) memberId", example = "10", required = true)
            @RequestParam Long memberId,

            @Parameter(description = "비밀번호 방이면 필요(4자리 숫자)", example = "1234", required = false)
            @RequestParam(required = false) String roomPassword
    ) {
        return chatRoomService.getRecentMessages(chatRoomId, memberId, roomPassword);
    }
}
