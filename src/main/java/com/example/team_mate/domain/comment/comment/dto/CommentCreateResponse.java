package com.example.team_mate.domain.comment.comment.dto;

import com.example.team_mate.domain.comment.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CommentCreateResponse {

    @Schema(description = "댓글 ID", example = "1")
    private Long id;

    @Schema(description = "댓글 내용", example = "게시글 내용 확인했습니다.")
    private String content;

    @Schema(description = "작성자 ID", example = "likelion2222")
    private String authorUsername;

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    public CommentCreateResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.authorUsername = comment.getAuthor().getUsername();
        this.postId = comment.getPost().getId();
    }
}
