package com.example.team_mate.domain.post.post.controller;

import com.example.team_mate.config.CustomUserDetails;
import com.example.team_mate.domain.post.post.dto.PostCreateRequest;
import com.example.team_mate.domain.post.post.dto.PostResponse;
import com.example.team_mate.domain.post.post.entity.Post;
import com.example.team_mate.domain.post.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "게시글(Post) API", description = "프로젝트 게시글 및 투표 관련 API")
public class PostController {

    private final PostService postService;
    private final ObjectMapper objectMapper;

    /** 게시글 생성 (파일 + 투표 포함) */
    @PostMapping(value = "/projects/{projectId}/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시글 생성", description = "게시글을 작성합니다. (파일 첨부 및 투표 생성 가능)")
    public ResponseEntity<PostResponse> createPost(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(
                    description = "게시글 정보 (JSON)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    type = "string",
                                    example = "{\n" +
                                            "  \"title\": \"점심 메뉴 투표\",\n" +
                                            "  \"content\": \"오늘 점심 뭐 먹을까요?\",\n" +
                                            "  \"poll\": {\n" +
                                            "    \"title\": \"메뉴 선택\",\n" +
                                            "    \"allowMultiple\": false,\n" +
                                            "    \"options\": [\n" +
                                            "      { \"text\": \"짜장면\" },\n" +
                                            "      { \"text\": \"짬뽕\" },\n" +
                                            "      { \"text\": \"탕수육\" }\n" +
                                            "    ]\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            )
            @RequestPart("request") String requestStr,

            @Parameter(description = "첨부 파일 리스트 (다중 선택 가능)", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {

        // 문자열 -> 객체 수동 변환
        PostCreateRequest request = objectMapper.readValue(requestStr, PostCreateRequest.class);

        PostResponse response = postService.createPost(projectId, userDetails.getUsername(), request, files);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** 게시글 목록 조회 */
    @GetMapping("/projects/{projectId}/posts")
    @Operation(summary = "게시글 목록 조회", description = "해당 프로젝트의 모든 게시글을 조회합니다.")
    public ResponseEntity<List<PostResponse>> getPosts(
            @PathVariable Long projectId
    ) {
        List<Post> posts = postService.getPostsByProject(projectId);
        List<PostResponse> response = posts.stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /** 게시글 상세 조회 */
    @GetMapping("/posts/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 상세 정보를 조회합니다.")
    public ResponseEntity<PostResponse> getPostDetail(
            @PathVariable Long postId
    ) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(PostResponse.from(post));
    }

    /** 게시글 수정 */
    @PutMapping("/posts/{postId}")
    @Operation(summary = "게시글 수정", description = "작성자만 수정 가능합니다.")
    public ResponseEntity<String> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PostCreateRequest request
    ) {
        postService.updatePost(postId, userDetails.getUsername(), request.getTitle(), request.getContent());
        return ResponseEntity.ok("게시글이 수정되었습니다.");
    }

    /** 게시글 삭제 */
    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "게시글 삭제", description = "작성자만 삭제 가능합니다.")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        postService.deletePost(postId, userDetails.getUsername());
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    /** 게시글 상단 고정 토글 */
    @PostMapping("/posts/{postId}/pin")
    @Operation(summary = "게시글 상단 고정 토글", description = "게시글의 상단 고정 여부를 반전시킵니다.")
    public ResponseEntity<String> togglePin(@PathVariable Long postId) {
        postService.togglePin(postId);
        return ResponseEntity.ok("토글 성공");
    }

    /** 투표하기 */
    @PostMapping("/posts/{postId}/vote")
    @Operation(summary = "투표하기", description = "게시글의 투표 항목에 투표합니다.")
    public ResponseEntity<String> vote(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody List<Long> pollOptionIds
    ) {
        postService.vote(pollOptionIds, userDetails.getUsername());
        return ResponseEntity.ok("투표 완료");
    }
}