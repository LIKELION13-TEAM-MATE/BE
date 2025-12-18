package com.example.team_mate.domain.post.post.controller;

import com.example.team_mate.domain.post.post.entity.Post;
import com.example.team_mate.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /** 게시물 작성 form */
    @GetMapping("/project/{projectId}/post/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> showWriteForm(@PathVariable Long projectId) {
        // 프론트엔드에 프로젝트 ID 전달
        Map<String, Object> response = new HashMap<>();
        response.put("projectId", projectId);
        return ResponseEntity.ok(response);
    }

    /** 게시글 작성 처리 */
    @PostMapping("/project/{projectId}/post/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createPost(
            @PathVariable Long projectId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) List<MultipartFile> files,
            @RequestParam(required = false) String pollTitle,
            @RequestParam(required = false) List<String> pollOptions,
            @RequestParam(required = false) LocalDate pollEndDate,
            @RequestParam(required = false, defaultValue = "false") boolean pollAllowMultiple,
            Authentication authentication
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            postService.createPost(
                    projectId, authentication.getName(), title, content,
                    files, pollTitle, pollOptions, pollEndDate, pollAllowMultiple
            );

            // 성공 메시지와 리다이렉트할 ID 반환
            response.put("message", "게시글이 성공적으로 작성되었습니다.");
            response.put("projectId", projectId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IOException e) {
            // 실패 메시지 반환
            response.put("error", "게시글 작성 실패");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /** 투표 처리 */
    @PostMapping("/post/vote")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> vote(
            @RequestParam Long projectId,
            @RequestParam(required = false) List<Long> pollOptionIds,
            @RequestParam Long postId,
            Authentication authentication
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 누가 투표했는지
            postService.vote(pollOptionIds, authentication.getName());

            response.put("message", "투표가 완료되었습니다.");
            response.put("projectId", projectId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // "이미 참여한 투표입니다" 같은 에러 메시지를 전달
            response.put("error", "투표 실패");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    /** 게시글 수정 form */
    @GetMapping("/post/{postId}/edit")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> showEditForm(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("post", post); // Post 엔티티 반환 (순환 참조 주의 필요)
        response.put("projectId", post.getProject().getId());

        return ResponseEntity.ok(response);
    }

    /** 게시글 수정 */
    @PostMapping("/post/{postId}/edit")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePost(
            @PathVariable Long postId,
            @RequestParam String title,
            @RequestParam String content,
            Authentication authentication
    ) {
        postService.updatePost(postId, authentication.getName(), title, content);

        Post post = postService.getPostById(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Post Updated");
        response.put("projectId", post.getProject().getId());

        return ResponseEntity.ok(response);
    }

    /** 게시글 삭제 */
    @PostMapping("/post/{postId}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Post post = postService.getPostById(postId);
        Long projectId = post.getProject().getId();

        postService.deletePost(postId, authentication.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Post Deleted");
        response.put("projectId", projectId);

        return ResponseEntity.ok(response);
    }

    /** 게시글 고정 */
    @PostMapping("/post/{postId}/pin")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> togglePin(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        postService.togglePin(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Pin Toggled");
        response.put("projectId", post.getProject().getId());

        return ResponseEntity.ok(response);
    }
}