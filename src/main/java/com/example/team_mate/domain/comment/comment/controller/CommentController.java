package com.example.team_mate.domain.comment.comment.controller;

import com.example.team_mate.domain.comment.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /** 댓글 작성 */
    @PostMapping("/post/{postId}/comment")
    @ResponseBody
    public ResponseEntity<String> writeComment(
            @PathVariable Long postId,
            @RequestParam String content,
            Authentication authentication
    ) {
        commentService.writeComment(postId, authentication.getName(), content);

        // 성공 메시지 반환
        return ResponseEntity.ok("{\"message\": \"Comment Created\"}");
    }

    /** 댓글 삭제 */
    @PostMapping("/comment/{commentId}/delete") // REST 방식이라면 @DeleteMapping을 권장하지만, 일단 기존 유지
    @ResponseBody
    public ResponseEntity<String> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        commentService.deleteComment(commentId, authentication.getName());

        // 성공 메시지 반환
        return ResponseEntity.ok("{\"message\": \"Comment Deleted\"}");
    }
}