package com.example.team_mate.domain.comment.comment.controller;

import com.example.team_mate.domain.comment.comment.dto.CommentCreateRequest;
import com.example.team_mate.domain.comment.comment.dto.CommentCreateResponse;
import com.example.team_mate.domain.comment.comment.entity.Comment;
import com.example.team_mate.domain.comment.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; // 추가
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@Tag(name = "Comment", description = "게시글의 댓글 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class CommentApiController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{postId}")
    @Operation(
            summary = "댓글 작성",
            description = "게시글에 댓글을 작성합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "댓글 작성 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CommentCreateResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "게시글 없음")
            }
    )
    public ResponseEntity<CommentCreateResponse> writeComment( // ResponseEntity로 변경
                                                               @Parameter(description = "댓글을 작성할 게시글 ID")
                                                               @PathVariable Long postId,

                                                               @RequestBody CommentCreateRequest request,

                                                               Authentication authentication
    ) {
        Comment comment = commentService.writeComment(postId, authentication.getName(), request.getContent());
        return ResponseEntity.ok(new CommentCreateResponse(comment));
    }


    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    @Operation(
            summary = "댓글 삭제",
            description = "내가 작성한 댓글을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
                    @ApiResponse(responseCode = "404", description = "댓글 없음")
            }
    )
    public ResponseEntity<String> deleteComment( // Void -> String 변경 (JSON 반환)
                                                 @Parameter(description = "삭제할 댓글 ID")
                                                 @PathVariable Long commentId,

                                                 Authentication authentication
    ) {
        commentService.deleteComment(commentId, authentication.getName());

        // 프론트엔드 JSON 파싱 에러 방지를 위해 명확한 JSON 메시지 반환
        return ResponseEntity.ok("{\"message\": \"Comment Deleted\"}");
    }
}