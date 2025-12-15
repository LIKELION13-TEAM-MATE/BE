package com.example.team_mate.domain.chatroom.chatroom.entity;

import com.example.team_mate.domain.project.project.entity.Project;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ChatRoom(Project project, String name, String passwordHash) {
        this.project = project;
        this.name = name;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isPasswordEnabled() {
        return passwordHash != null && !passwordHash.isBlank();
    }
}
