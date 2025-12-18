package com.example.team_mate.domain.chatroom.chatroom.controller;

import com.example.team_mate.domain.chatroom.chatroom.entity.ChatRoom;
import com.example.team_mate.domain.chatroom.chatroom.repository.ChatRoomMemberRepository;
import com.example.team_mate.domain.chatroom.chatroom.repository.ChatRoomRepository;
import com.example.team_mate.domain.chatroom.chatroom.service.ChatRoomService;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project")
public class ChatRoomController {

    private final ProjectRepository projectRepository;
    private final ChatRoomService chatRoomService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final PasswordEncoder passwordEncoder;

    /** 채팅방 목록 조회 */
    @GetMapping("/{id}/chatrooms")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> chatrooms(@PathVariable Long id,
                                                         @RequestParam Long memberId) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // 프론트엔드에 필요한 데이터 조립
        Map<String, Object> response = new HashMap<>();
        response.put("projectId", project.getId());
        response.put("projectName", project.getProjectName());
        response.put("category", project.getCategory());

        // 채팅방 리스트 가져오기
        // (참고: ChatRoomService.getChatRooms가 DTO를 반환하는지 Entity를 반환하는지에 따라
        //  JSON 직렬화 문제가 생길 수도 있습니다. 만약 무한 참조 에러가 나면 DTO로 변환이 필요합니다.)
        response.put("chatRooms", chatRoomService.getChatRooms(id, memberId));

        return ResponseEntity.ok(response);
    }

    /** 채팅방 생성 폼 정보 조회 */
    @GetMapping("/{projectId}/chatrooms/new")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createForm(@PathVariable Long projectId,
                                                          @RequestParam Long memberId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("projectId", project.getId());
        response.put("projectName", project.getProjectName());

        return ResponseEntity.ok(response);
    }

    /**
     * 채팅방 입장 (유효성 검증)
     * 성공 시 JSON, 실패 시 에러 코드(400/403) 반환
     */
    @GetMapping("/chatrooms/{chatRoomId}")
    @ResponseBody
    public ResponseEntity<?> chatRoom(@PathVariable Long chatRoomId,
                                      @RequestParam Long memberId,
                                      @RequestParam(required = false) String roomPassword) {

        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found"));

        // 1) 초대 여부 체크
        if (!chatRoomMemberRepository.existsByChatRoom_IdAndMember_Id(chatRoomId, memberId)) {
            // [실패] 403 Forbidden: 초대받지 않음 -> 프론트에서 "입장 불가" 알림
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\": \"Forbidden\", \"message\": \"초대되지 않은 채팅방입니다.\"}");
        }

        // 비밀번호 방이면 입장 시점에서 비번 검증
        if (room.isPasswordEnabled()) {
            String input = (roomPassword == null) ? null : roomPassword.trim();

            if (input == null || input.isEmpty()) {

                return ResponseEntity.badRequest()
                        .body("{\"error\": \"Password Required\", \"message\": \"비밀번호가 필요한 채팅방입니다.\"}");
            }

            if (!passwordEncoder.matches(input, room.getPasswordHash())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Invalid Password\", \"message\": \"비밀번호가 일치하지 않습니다.\"}");
            }
        }

        // 모든 검증 통과 -> 입장 정보 반환
        long memberCount = chatRoomMemberRepository.countByChatRoom_Id(chatRoomId);

        Map<String, Object> response = new HashMap<>();
        response.put("chatRoomId", chatRoomId);
        response.put("chatRoomName", room.getName());
        response.put("memberCount", memberCount);
        response.put("projectId", room.getProject().getId());
        response.put("message", "Enter Success"); // 입장 허용 메시지

        return ResponseEntity.ok(response);
    }
}