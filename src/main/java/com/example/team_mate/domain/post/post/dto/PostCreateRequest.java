package com.example.team_mate.domain.post.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "프로젝트 내 게시글 생성/수정 요청")
public class PostCreateRequest {

    @Schema(description = "게시글 제목", example = "정규회의")
    private String title;

    @Schema(description = "게시글 내용", example = "API 연동을 위한 멋사 데모데이 회의 예정 ")
    private String content;

}
