package com.example.team_mate.domain.team.team.controller;

import com.example.team_mate.domain.team.team.dto.TeamActionResponse;
import com.example.team_mate.domain.team.team.dto.TeamInviteRequest;
import com.example.team_mate.domain.team.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 기존 TeamController(HTML redirect)와 별도로,
 * 같은 비즈니스 로직을 사용하는 REST API 버전 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Team", description = "프로젝트 팀원 초대/강퇴 관리 API")
@RequestMapping("/api/v1/projects/{projectId}/team")
public class TeamApiController {

    private final TeamService teamService;

    // 팀원 초대 (username 기반)
    @PostMapping(
            path = "/invite",
            consumes = "application/json",
            produces = "application/json"
    )
    @Operation(
            summary = "프로젝트 팀원 초대",
            description = "username(아이디)로 사용자를 찾아 해당 프로젝트의 팀원으로 초대합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "초대 성공",
                    content = @Content(schema = @Schema(implementation = TeamActionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "초대 실패 (존재하지 않는 유저, 이미 초대된 유저, 존재하지 않는 프로젝트 등)",
                    content = @Content(schema = @Schema(implementation = TeamActionResponse.class))
            )
    })
    public ResponseEntity<TeamActionResponse> inviteMember(
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId,
            @RequestBody TeamInviteRequest request
    ) {
        try {
            // 기존 서비스 로직 그대로 재사용
            teamService.inviteMember(projectId, request.getUsername());
            return ResponseEntity.ok(
                    new TeamActionResponse("팀원을 성공적으로 초대했습니다!")
            );
        } catch (IllegalArgumentException e) {
            // 서비스에서 던진 메시지를 그대로 내려줌
            return ResponseEntity.badRequest()
                    .body(new TeamActionResponse(e.getMessage()));
        }
    }

    // 팀원 강퇴 (membershipId 기반)
    @PostMapping(
            path = "/kick/{membershipId}",
            produces = "application/json"
    )
    @Operation(
            summary = "프로젝트 팀원 강퇴",
            description = "프로젝트 리더가 특정 팀원을 프로젝트에서 제거합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "강퇴 성공",
                    content = @Content(schema = @Schema(implementation = TeamActionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "강퇴 실패 (권한 없음, 자신 강퇴 시도, 참가 기록 없음 등)",
                    content = @Content(schema = @Schema(implementation = TeamActionResponse.class))
            )
    })
    public ResponseEntity<TeamActionResponse> kickMember(
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId,
            @Parameter(description = "강퇴할 팀원의 팀 멤버십 ID", example = "10")
            @PathVariable Long membershipId,
            Authentication authentication
    ) {
        // 요청한 사용자 username (리더인지 체크용)
        String requesterUsername = authentication.getName();

        try {
            teamService.kickMember(projectId, membershipId, requesterUsername);
            return ResponseEntity.ok(
                    new TeamActionResponse("팀원을 성공적으로 강퇴했습니다.")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new TeamActionResponse(e.getMessage()));
        }
    }
}