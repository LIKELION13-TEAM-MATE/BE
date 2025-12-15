package com.example.team_mate.domain.chatroom.chatroom.controller;

import com.example.team_mate.domain.chatroom.chatroom.entity.ChatRoom;
import com.example.team_mate.domain.chatroom.chatroom.repository.ChatRoomMemberRepository;
import com.example.team_mate.domain.chatroom.chatroom.repository.ChatRoomRepository;
import com.example.team_mate.domain.chatroom.chatroom.service.ChatRoomService;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project")
public class ChatRoomController {

    private final ProjectRepository projectRepository;
    private final ChatRoomService chatRoomService;

    // 추가: 입장 차단/비번검증을 위해 필요
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/{id}/chatrooms")
    public String chatrooms(@PathVariable Long id,
                            @RequestParam Long memberId,
                            Model model) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        model.addAttribute("project", project);
        model.addAttribute("projectId", project.getId());
        model.addAttribute("projectName", project.getProjectName());
        model.addAttribute("category", project.getCategory());
        model.addAttribute("memberId", memberId);

        model.addAttribute("chatRooms", chatRoomService.getChatRooms(id, memberId));

        return "chatroom/chatrooms";
    }

    @GetMapping("/{projectId}/chatrooms/new")
    public String createForm(@PathVariable Long projectId,
                             @RequestParam Long memberId, //  create 화면에서도 memberId 유지(필요하면 사용)
                             Model model) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        model.addAttribute("project", project);
        model.addAttribute("projectId", project.getId());
        model.addAttribute("projectName", project.getProjectName());
        model.addAttribute("category", project.getCategory());
        model.addAttribute("memberId", memberId);

        return "chatroom/chatroom-create";
    }

    /**
     * 채팅방 입장 페이지
     * - 초대되지 않은 사람: "초대되지 않은 채팅방입니다." 띄우고 목록으로 리다이렉트
     * - 비밀번호 방: roomPassword 없거나 틀리면 메시지 띄우고 목록으로 리다이렉트
     */
    @GetMapping("/chatrooms/{chatRoomId}")
    public String chatRoom(@PathVariable Long chatRoomId,
                           @RequestParam Long memberId,
                           @RequestParam(required = false) String roomPassword,
                           RedirectAttributes ra,
                           Model model) {

        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found"));

        Project project = room.getProject();

        // 1) 초대 여부 체크
        if (!chatRoomMemberRepository.existsByChatRoom_IdAndMember_Id(chatRoomId, memberId)) {
            ra.addFlashAttribute("errorMessage", "초대되지 않은 채팅방입니다.");
            return "redirect:/project/" + project.getId() + "/chatrooms?memberId=" + memberId;
        }

        // 2) 비밀번호 방이면 입장 시점에서 비번 검증
        if (room.isPasswordEnabled()) {
            String input = (roomPassword == null) ? null : roomPassword.trim();

            if (input == null || input.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "비밀번호가 필요한 채팅방입니다.");
                return "redirect:/project/" + project.getId() + "/chatrooms?memberId=" + memberId;
            }

            if (!passwordEncoder.matches(input, room.getPasswordHash())) {
                ra.addFlashAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
                return "redirect:/project/" + project.getId() + "/chatrooms?memberId=" + memberId;
            }
        }

        // 채팅방 이름 + 인원수
        long memberCount = chatRoomMemberRepository.countByChatRoom_Id(chatRoomId);

        // 정상 입장: chatroom.html에 필요한 값 세팅
        model.addAttribute("project", project);
        model.addAttribute("projectId", project.getId());
        model.addAttribute("projectName", project.getProjectName());
        model.addAttribute("category", project.getCategory());

        model.addAttribute("chatRoomId", chatRoomId);
        model.addAttribute("memberId", memberId);
        model.addAttribute("roomPassword", roomPassword);

        model.addAttribute("chatRoomName", room.getName());
        model.addAttribute("chatRoomMemberCount", memberCount);

        return "chatroom/chatroom";
    }
}
