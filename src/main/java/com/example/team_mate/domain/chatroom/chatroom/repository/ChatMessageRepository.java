package com.example.team_mate.domain.chatroom.chatroom.repository;

import com.example.team_mate.domain.chatroom.chatroom.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findTop50ByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);

    @Query("""
        select cm.chatRoom.id, cm.content, cm.createdAt
        from ChatMessage cm
        where cm.chatRoom.project.id = :projectId
          and cm.createdAt = (
              select max(cm2.createdAt)
              from ChatMessage cm2
              where cm2.chatRoom.id = cm.chatRoom.id
          )
    """)
    List<Object[]> findLastMessagesByProject(@Param("projectId") Long projectId);
}
