package com.example.team_mate.domain.chatroom.chatroom.service;

public interface ProjectMemberChecker {
    boolean isProjectMember(Long projectId, Long memberId);
}
