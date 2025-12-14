package com.example.team_mate.domain.chatroom.chatroom.repository;

import com.example.team_mate.domain.chatroom.chatroom.entity.ChatRoomMember;
import com.example.team_mate.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    // 이미 쓰고 있는 초대 여부 체크
    boolean existsByChatRoom_IdAndMember_Id(Long chatRoomId, Long memberId);
    // 채팅방 초대 인원 수
    long countByChatRoom_Id(Long chatRoomId);

    // 이미 있는 프로젝트 안 채팅방별 인원수 집계
    @Query("""
        select crm.chatRoom.id, count(crm.id)
        from ChatRoomMember crm
        where crm.chatRoom.project.id = :projectId
        group by crm.chatRoom.id
    """)
    List<Object[]> countMembersByProject(@Param("projectId") Long projectId);

    // 특정 채팅방에 초대된 멤버 목록(아이디 순)
    @Query("""
        select crm.member
        from ChatRoomMember crm
        where crm.chatRoom.id = :chatRoomId
        order by crm.member.id asc
    """)
    List<Member> findMembersByChatRoomIdOrderByMemberIdAsc(@Param("chatRoomId") Long chatRoomId);

}
