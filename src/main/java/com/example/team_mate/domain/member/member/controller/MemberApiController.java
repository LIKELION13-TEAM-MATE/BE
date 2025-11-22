package com.example.team_mate.domain.member.member.controller;

import com.example.team_mate.domain.member.member.dto.MemberLoginRequest;
import com.example.team_mate.domain.member.member.dto.MemberSignUpRequest;
import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 정보 API")
@RequestMapping("/api/v1/members")
public class MemberApiController {

    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    @Operation(
            summary = "회원가입",
            description = "사용자를 회원가입시킵니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공"),
                    @ApiResponse(responseCode = "400", description = "회원가입 실패")
            }
    )
    public String signUp(@RequestBody MemberSignUpRequest request) {
        memberService.signup(
                request.getUsername(),
                request.getNickname(),
                request.getPassword(),
                request.getPassword()
        );

        return "회원가입 성공";
    }

    // 로그인
    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "로그인하여 사용자 정보를 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "401", description = "로그인 실패")
            }
    )
    public ResponseEntity<LoginResponse> login(@RequestBody MemberLoginRequest request) {

        Member member = memberService.login(request.getUsername(), request.getPassword());

        if (member == null) {
            return ResponseEntity.status(401).body(null);
        }

        LoginResponse response = new LoginResponse(
                member.getId(),
                member.getUsername(),
                member.getNickname()
        );

        return ResponseEntity.ok(response);
    }

    @Getter
    public static class LoginResponse {

        @Schema(description = "회원 ID", example = "1")
        private final Long id;

        @Schema(description = "아이디", example = "likelion1111")
        private final String username;

        @Schema(description = "닉네임", example = "김멋사")
        private final String nickname;

        public LoginResponse(Long id, String username, String nickname) {
            this.id = id;
            this.username = username;
            this.nickname = nickname;
        }
    }
}
