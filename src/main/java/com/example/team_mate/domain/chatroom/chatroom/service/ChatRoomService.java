package com.example.team_mate.domain.chatroom.chatroom.service;

import com.example.team_mate.domain.chatroom.chatroom.dto.*;
import com.example.team_mate.domain.chatroom.chatroom.entity.*;
import com.example.team_mate.domain.chatroom.chatroom.repository.*;
import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.repository.MemberRepository;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.repository.ProjectRepository;
import com.example.team_mate.domain.team.team.entity.TeamMembership;
import com.example.team_mate.domain.team.team.repository.TeamMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final TeamMembershipRepository teamMembershipRepository;

    private final ProjectMemberChecker projectMemberChecker;
    private final PasswordEncoder passwordEncoder;

    // 프로젝트 참여자 목록(초대 dropdown용)
    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getProjectMembers(Long projectId, Long requesterMemberId) {
        if (!projectMemberChecker.isProjectMember(projectId, requesterMemberId)) {
            throw new IllegalArgumentException("프로젝트 참여자만 조회 가능");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // 리더 + TeamMembership 멤버들 합치기(중복 제거)
        Map<Long, ProjectMemberResponse> map = new LinkedHashMap<>();

        if (project.getMember() != null) {
            Member leader = project.getMember();
            map.put(leader.getId(), new ProjectMemberResponse(leader.getId(), leader.getNickname()));
        }

        List<TeamMembership> memberships = teamMembershipRepository.findByProject(project);
        for (TeamMembership tm : memberships) {
            Member m = tm.getMember();
            if (m != null) {
                map.put(m.getId(), new ProjectMemberResponse(m.getId(), m.getNickname()));
            }
        }

        return new ArrayList<>(map.values());
    }

    // 채팅방 생성
    public Long createChatRoom(Long projectId, Long creatorMemberId, ChatRoomCreateRequest req) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (!projectMemberChecker.isProjectMember(projectId, creatorMemberId)) {
            throw new IllegalArgumentException("프로젝트 참여자만 채팅방 생성 가능");
        }

        String name = normalize(req.name());
        if (name == null) throw new IllegalArgumentException("대화방 이름은 필수");

        String pw = normalize(req.password());
        if (pw != null && !pw.matches("^\\d{4}$")) {
            throw new IllegalArgumentException("비밀번호는 4자리 숫자만 가능");
        }

        String hash = (pw == null) ? null : passwordEncoder.encode(pw);
        ChatRoom room = chatRoomRepository.save(new ChatRoom(project, name, hash));

        // creator + inviteMemberIds (중복 제거)
        Set<Long> memberIds = new LinkedHashSet<>();
        memberIds.add(creatorMemberId);
        if (req.inviteMemberIds() != null) memberIds.addAll(req.inviteMemberIds());

        // 초대 멤버는 “프로젝트 참여자”만
        for (Long memberId : memberIds) {
            if (!projectMemberChecker.isProjectMember(projectId, memberId)) {
                throw new IllegalArgumentException("프로젝트 참여자만 초대 가능: memberId=" + memberId);
            }
        }

        for (Long memberId : memberIds) {
            Member m = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));
            chatRoomMemberRepository.save(new ChatRoomMember(room, m));
        }

        return room.getId();
    }

    // 프로젝트 내 채팅방 목록
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatRooms(Long projectId, Long memberId) {
        if (!projectMemberChecker.isProjectMember(projectId, memberId)) {
            throw new IllegalArgumentException("프로젝트 참여자만 조회 가능");
        }

        // 채팅방별 인원수
        Map<Long, Long> countMap = new HashMap<>();
        for (Object[] row : chatRoomMemberRepository.countMembersByProject(projectId)) {
            Long roomId = (Long) row[0];
            Long cnt = (Long) row[1];
            countMap.put(roomId, cnt);
        }

        // 채팅방별 최근 메시지/시간
        Map<Long, String> lastMsgMap = new HashMap<>();
        Map<Long, java.time.LocalDateTime> lastAtMap = new HashMap<>();

        for (Object[] row : chatMessageRepository.findLastMessagesByProject(projectId)) {
            Long roomId = (Long) row[0];
            String content = (String) row[1];
            java.time.LocalDateTime createdAt = (java.time.LocalDateTime) row[2];

            lastMsgMap.putIfAbsent(roomId, content);
            lastAtMap.putIfAbsent(roomId, createdAt);
        }

        java.time.LocalDate today = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
        java.time.format.DateTimeFormatter timeFmt =
                java.time.format.DateTimeFormatter.ofPattern("a h:mm", java.util.Locale.KOREAN);
        java.time.format.DateTimeFormatter dateFmt =
                java.time.format.DateTimeFormatter.ofPattern("M월 d일", java.util.Locale.KOREAN);

        // 1) 프로젝트 채팅방 전부 가져오기 (정렬은 우리가 직접)
        List<ChatRoom> rooms = chatRoomRepository.findByProjectId(projectId);

        // 2) “최근 메시지 시간” 기준으로 내림차순 정렬
        rooms.sort((a, b) -> {
            java.time.LocalDateTime aLast = lastAtMap.get(a.getId());
            java.time.LocalDateTime bLast = lastAtMap.get(b.getId());

            // 둘 다 최근 메시지가 있으면 그것으로 비교
            if (aLast != null && bLast != null) return bLast.compareTo(aLast);

            // 한쪽만 있으면 있는 쪽이 위
            if (aLast == null && bLast != null) return 1;
            if (aLast != null && bLast == null) return -1;

            // 둘 다 메시지가 없으면 방 생성시간(또는 id)로 최신 우선
            if (a.getCreatedAt() != null && b.getCreatedAt() != null) return b.getCreatedAt().compareTo(a.getCreatedAt());
            return Long.compare(b.getId(), a.getId());
        });

        // 3) DTO 변환
        return rooms.stream()
                .map(r -> {
                    Long roomId = r.getId();

                    List<MemberAvatarResponse> avatars =
                            chatRoomMemberRepository.findMembersByChatRoomIdOrderByMemberIdAsc(roomId)
                                    .stream()
                                    .limit(4)
                                    .map(m -> new MemberAvatarResponse(
                                            m.getId(),
                                            m.getNickname(),
                                            m.getInitial(),
                                            m.getAvatarColor()
                                    ))
                                    .toList();

                    String lastMsg = lastMsgMap.getOrDefault(roomId, "");
                    if (lastMsg != null && lastMsg.length() > 30) {
                        lastMsg = lastMsg.substring(0, 30) + "...";
                    }

                    java.time.LocalDateTime lastAt = lastAtMap.get(roomId);
                    String lastDisplayTime = "";
                    if (lastAt != null) {
                        if (lastAt.toLocalDate().isEqual(today)) {
                            lastDisplayTime = lastAt.format(timeFmt);
                        } else {
                            lastDisplayTime = lastAt.format(dateFmt);
                        }
                    }

                    return new ChatRoomResponse(
                            roomId,
                            r.getName(),
                            r.isPasswordEnabled(),
                            r.getCreatedAt(),
                            countMap.getOrDefault(roomId, 0L),
                            avatars,
                            lastMsg,
                            lastDisplayTime
                    );
                })
                .toList();
    }

    private void validateInvited(Long chatRoomId, Long memberId) {
        if (!chatRoomMemberRepository.existsByChatRoom_IdAndMember_Id(chatRoomId, memberId)) {
            throw new IllegalArgumentException("초대되지 않은 채팅방입니다.");
        }
    }

    // 메시지 전송(비번방이면 비번 검증)
    public Long sendMessage(Long chatRoomId, Long senderMemberId, ChatMessageSendRequest req) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found"));

        validateInvited(chatRoomId, senderMemberId);

        if (room.isPasswordEnabled()) {
            String input = normalize(req.roomPassword());
            if (input == null) throw new IllegalArgumentException("비밀번호가 필요한 채팅방");
            if (!passwordEncoder.matches(input, room.getPasswordHash())) {
                throw new IllegalArgumentException("비밀번호 불일치");
            }
        }

        String content = normalize(req.content());
        if (content == null) throw new IllegalArgumentException("메시지 내용은 필수");

        Member sender = memberRepository.findById(senderMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        ChatMessage msg = chatMessageRepository.save(new ChatMessage(room, sender, content));
        return msg.getId();
    }

    // 최근 메시지 50개 조회(확인용)
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getRecentMessages(Long chatRoomId, Long memberId, String roomPassword) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found"));

        validateInvited(chatRoomId, memberId);

        if (room.isPasswordEnabled()) {
            String input = normalize(roomPassword);
            if (input == null) throw new IllegalArgumentException("비밀번호가 필요한 채팅방");
            if (!passwordEncoder.matches(input, room.getPasswordHash())) {
                throw new IllegalArgumentException("비밀번호 불일치");
            }
        }

        List<ChatMessage> desc = chatMessageRepository.findTop50ByChatRoomIdOrderByCreatedAtDesc(chatRoomId);
        Collections.reverse(desc);

        return desc.stream()
                .map(m -> new ChatMessageResponse(
                        m.getId(),
                        m.getSender().getId(),
                        m.getSender().getNickname(),
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ChatRoomPageHeaderResponse getChatRoomPageHeader(Long chatRoomId, Long memberId) {

        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found"));

        // 초대 여부 체크 (초대 안 된 사람은 제목조차 못 보게 하려면 여기서 막는게 맞음)
        if (!chatRoomMemberRepository.existsByChatRoom_IdAndMember_Id(chatRoomId, memberId)) {
            throw new IllegalArgumentException("초대되지 않은 채팅방입니다.");
        }

        long count = chatRoomMemberRepository.countByChatRoom_Id(chatRoomId);

        return new ChatRoomPageHeaderResponse(
                room.getProject().getProjectName(),
                room.getName(),
                count
        );
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
