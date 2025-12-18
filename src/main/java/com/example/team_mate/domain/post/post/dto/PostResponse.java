package com.example.team_mate.domain.post.post.dto;

import com.example.team_mate.domain.post.post.entity.Post;
import com.example.team_mate.domain.poll.poll.entity.Poll;
import com.example.team_mate.domain.poll.poll.entity.PollOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

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

    @Schema(description = "투표 정보 (없으면 null)")
    private PollResponse poll;

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorUsername(post.getAuthor().getUsername())
                .projectId(post.getProject().getId())
                .pinned(post.isPinned())
                .poll(post.getPoll() != null ? PollResponse.from(post.getPoll()) : null)
                .build();
    }

    @Getter
    @Builder
    public static class PollResponse {
        private Long id;
        private String title;
        private boolean allowMultiple;
        private List<PollOptionResponse> options;

        public static PollResponse from(Poll poll) {
            return PollResponse.builder()
                    .id(poll.getId())
                    .title(poll.getTitle())
                    .allowMultiple(poll.isAllowMultiple())
                    .options(poll.getOptions().stream()
                            .map(PollOptionResponse::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class PollOptionResponse {
        private Long id;
        private String text;

        public static PollOptionResponse from(PollOption option) {
            return PollOptionResponse.builder()
                    .id(option.getId())
                    .text(option.getOptionText())
                    .build();
        }
    }
}