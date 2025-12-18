package com.example.team_mate.domain.comment.comment.dto;

import com.example.team_mate.domain.comment.comment.entity.Comment;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CommentListResponse {
    private Long id;
    private String content;
    private String nickname;   // 작성자 닉네임
    private String username;   // 작성자 아이디 (수정/삭제 권한 체크용)
    private String initial;      // 닉네임 첫 글자 (추가)
    private String avatarColor;
    private LocalDateTime createdDate;

    public CommentListResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.nickname = comment.getAuthor().getNickname();
        this.username = comment.getAuthor().getUsername();
        this.initial = comment.getAuthor().getInitial();
        this.avatarColor = comment.getAuthor().getAvatarColor();
        this.createdDate = comment.getCreatedDate();
    }
}