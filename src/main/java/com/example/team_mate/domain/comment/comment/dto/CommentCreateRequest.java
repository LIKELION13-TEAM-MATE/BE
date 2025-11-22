package com.example.team_mate.domain.comment.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "댓글 생성 요청 DTO")
public class CommentCreateRequest {

    @Schema(
            description = "댓글 내용",
            example = "게시글 내용 확인했습니다."
    )
    private String content;
}
