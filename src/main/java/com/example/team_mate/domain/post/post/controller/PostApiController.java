package com.example.team_mate.domain.post.post.controller;

import com.example.team_mate.domain.post.post.dto.PostCreateRequest;
import com.example.team_mate.domain.post.post.dto.PostResponse;
import com.example.team_mate.domain.post.post.entity.Post;
import com.example.team_mate.domain.post.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Post", description = "프로젝트의 게시글 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class PostApiController {

    private final PostService postService;

    // ===================== 목록 ===================== //
    @Operation(summary = "게시글 목록 조회", description = "특정 프로젝트의 게시글 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponse.class)))
            )
    })
    @GetMapping(
            value = "/projects/{projectId}/posts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<PostResponse>> getPostsByProject(
            @PathVariable Long projectId
    ) {
        List<Post> posts = postService.getPostsByProject(projectId);

        List<PostResponse> responses = posts.stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // ===================== 생성 ===================== //
    @Operation(summary = "게시글 생성", description = "새 게시글을 생성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping(
            value = "/projects/{projectId}/posts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PostResponse> createPost(
            @PathVariable Long projectId,
            @RequestBody PostCreateRequest request,
            Authentication authentication
    ) throws Exception {

        String username = authentication.getName();

        // (참고: 파일/투표 기능은 null로 처리되어 있습니다. 추후 기능 추가 시 수정 필요)
        postService.createPost(
                projectId,
                username,
                request.getTitle(),
                request.getContent(),
                null,    // files
                null,    // pollTitle
                null,    // pollOptions
                null,    // pollEndDate
                false    // pollAllowMultiple
        );

        // 생성된 Post ID를 알기 위해, 최신 글 1개 다시 조회 (서비스가 void 반환이라 불가피한 로직)
        List<Post> latest = postService.getPostsByProject(projectId);
        Post created = latest.get(0);

        return ResponseEntity
                .created(URI.create("/api/v1/posts/" + created.getId()))
                .body(PostResponse.from(created));
    }

    // ===================== 단건 조회 ===================== //
    @Operation(summary = "게시글 상세 조회", description = "게시글 한 건을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @GetMapping(
            value = "/posts/{postId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PostResponse> getPostDetail(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(PostResponse.from(post));
    }

    // ===================== 수정 ===================== //
    @Operation(summary = "게시글 수정", description = "게시글의 제목과 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PutMapping(
            value = "/posts/{postId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody PostCreateRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();

        postService.updatePost(
                postId,
                username,
                request.getTitle(),
                request.getContent()
        );

        Post updated = postService.getPostById(postId);
        return ResponseEntity.ok(PostResponse.from(updated));
    }

    // ===================== 삭제 ===================== //
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"), // 204 -> 200으로 변경
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<String> deletePost( // Void -> String으로 변경
                                              @PathVariable Long postId,
                                              Authentication authentication
    ) {
        String username = authentication.getName();
        postService.deletePost(postId, username);

        // 프론트엔드 JSON 파싱 에러 방지를 위해 메시지 반환
        return ResponseEntity.ok("{\"message\": \"Post Deleted\"}");
    }

    // ===================== 고정 토글 ===================== //
    @Operation(summary = "게시글 상단 고정 토글", description = "게시글의 상단 고정 여부를 반전시킵니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "토글 성공",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))
            )
    })
    @PostMapping(
            value = "/posts/{postId}/pin",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PostResponse> togglePin(@PathVariable Long postId) {
        postService.togglePin(postId);
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(PostResponse.from(post));
    }

    // ===================== 투표 ===================== //
    @Operation(summary = "투표하기", description = "게시글의 투표 항목에 투표합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "투표 완료")
    })
    @PostMapping(
            value = "/posts/{postId}/vote",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> vote( // Void -> String으로 변경
                                        @PathVariable Long postId,
                                        @RequestBody List<Long> pollOptionIds,
                                        Authentication authentication
    ) {
        String username = authentication.getName();
        postService.vote(pollOptionIds, username);

        // 프론트엔드 JSON 파싱 에러 방지를 위해 메시지 반환
        return ResponseEntity.ok("{\"message\": \"Vote Success\"}");
    }
}