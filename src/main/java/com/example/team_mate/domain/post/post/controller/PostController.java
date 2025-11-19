package com.example.team_mate.domain.post.post.controller;

import com.example.team_mate.domain.post.post.entity.Post;
import com.example.team_mate.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /** 게시물 작성 form */
    @GetMapping("/project/{projectId}/post/create")
    public String showWriteForm(@PathVariable Long projectId, Model model) {
        model.addAttribute("projectId", projectId);
        return "post/create";
    }

    /** 게시글 작성 처리 */
    @PostMapping("/project/{projectId}/post/create")
    public String createPost(
            @PathVariable Long projectId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) List<MultipartFile> files,
            @RequestParam(required = false) String pollTitle,
            @RequestParam(required = false) List<String> pollOptions,
            @RequestParam(required = false) LocalDate pollEndDate,
            @RequestParam(required = false, defaultValue = "false") boolean pollAllowMultiple,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            postService.createPost(
                    projectId, authentication.getName(), title, content,
                    files, pollTitle, pollOptions, pollEndDate, pollAllowMultiple
            );
            redirectAttributes.addFlashAttribute("successMessage", "게시글이 성공적으로 작성되었습니다.");
        } catch (IllegalArgumentException | IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "게시글 작성 실패: " + e.getMessage());
        }
        return "redirect:/project/detail/" + projectId;
    }

    /** 투표 처리 */
    @PostMapping("/post/vote")
    public String vote(
            @RequestParam Long projectId,
            @RequestParam(required = false) List<Long> pollOptionIds,
            @RequestParam Long postId,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // 누가 투표했는지
            postService.vote(pollOptionIds, authentication.getName());
            redirectAttributes.addFlashAttribute("pollSuccessMessage", "투표가 완료되었습니다.");

        } catch (IllegalArgumentException e) {
            // "이미 참여한 투표입니다" 같은 에러 메시지를 전달
            redirectAttributes.addFlashAttribute("pollErrorMessage", e.getMessage());
        }

        redirectAttributes.addFlashAttribute("targetPostId", postId);
        return "redirect:/project/detail/" + projectId;
    }


    /** 게시글 수정 form */
    @GetMapping("/post/{postId}/edit")
    public String showEditForm(@PathVariable Long postId, Model model) {
        Post post = postService.getPostById(postId);
        model.addAttribute("post", post);
        model.addAttribute("projectId", post.getProject().getId());
        return "post/edit";
    }

    /** 게시글 수정 */
    @PostMapping("/post/{postId}/edit")
    public String updatePost(
            @PathVariable Long postId,
            @RequestParam String title,
            @RequestParam String content,
            Authentication authentication
    ) {
        postService.updatePost(postId, authentication.getName(), title, content);

        Post post = postService.getPostById(postId);
        return "redirect:/project/detail/" + post.getProject().getId();
    }

    /** 게시글 삭제 */
    @PostMapping("/post/{postId}/delete")
    public String deletePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Post post = postService.getPostById(postId);
        Long projectId = post.getProject().getId();

        postService.deletePost(postId, authentication.getName());

        return "redirect:/project/detail/" + projectId;
    }

    /** 게시글 고정 */
    @PostMapping("/post/{postId}/pin")
    public String togglePin(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        postService.togglePin(postId);

        return "redirect:/project/detail/" + post.getProject().getId();
    }
}