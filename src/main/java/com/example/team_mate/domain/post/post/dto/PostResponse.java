package com.example.team_mate.domain.post.post.dto;

import com.example.team_mate.domain.post.post.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "게시글 응답")
public class PostResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(description = "제목", example = "정규회의")
    private String title;

    @Schema(description = "내용", example = "API 연동을 위한 멋사 데모데이 회의 예정")
    private String content;

    @Schema(description = "작성자 ID", example = "likelion1111")
    private String authorUsername;

    @Schema(description = "속한 프로젝트 ID", example = "1")
    private Long projectId;

    @Schema(description = "상단 고정 여부", example = "false")
    private boolean pinned;

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorUsername(post.getAuthor().getUsername())
                .projectId(post.getProject().getId())
                .pinned(post.isPinned())
                .build();
    }
}
