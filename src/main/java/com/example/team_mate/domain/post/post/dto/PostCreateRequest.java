package com.example.team_mate.domain.post.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "프로젝트 내 게시글 생성/수정 요청")
public class PostCreateRequest {

    @Schema(description = "게시글 제목", example = "정규회의")
    private String title;

    @Schema(description = "게시글 내용", example = "API 연동 회의")
    private String content;

    @Schema(description = "투표 생성 정보 (선택)")
    private PollCreateDto poll;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PollCreateDto {
        @Schema(description = "투표 제목", example = "점심 메뉴")
        private String title;

        @Schema(description = "복수 선택 가능 여부", example = "false")
        private boolean allowMultiple;

        @Schema(description = "투표 항목 리스트")
        private List<PollOptionDto> options;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PollOptionDto {
        @Schema(description = "항목 텍스트", example = "짜장면")
        private String text;
    }
}