package com.example.team_mate.domain.comment.comment.controller;

import com.example.team_mate.domain.comment.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /** 댓글 작성 */
    @PostMapping("/post/{postId}/comment")
    public String writeComment(
            @PathVariable Long postId,
            @RequestParam String content,
            @RequestParam Long projectId, // 리다이렉트용
            Authentication authentication
    ) {
        commentService.writeComment(postId, authentication.getName(), content);
        return "redirect:/project/detail/" + projectId;
    }

    /** 댓글 삭제 */
    @PostMapping("/comment/{commentId}/delete")
    public String deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long projectId, // 리다이렉트용
            Authentication authentication
    ) {
        commentService.deleteComment(commentId, authentication.getName());
        return "redirect:/project/detail/" + projectId;
    }
}