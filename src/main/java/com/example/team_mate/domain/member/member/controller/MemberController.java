package com.example.team_mate.domain.member.member.controller;

import com.example.team_mate.config.CustomUserDetails;
import com.example.team_mate.domain.member.member.dto.MemberLoginRequest;
import com.example.team_mate.domain.member.member.dto.MemberSignUpRequest;
import com.example.team_mate.domain.member.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;

    public MemberController(MemberService memberService, AuthenticationManager authenticationManager) {
        this.memberService = memberService;
        this.authenticationManager = authenticationManager;
    }

    /** 회원가입 페이지 */
    @GetMapping("/signup")
    @ResponseBody
    public ResponseEntity<Map<String, String>> signupForm() {
        return ResponseEntity.ok(Collections.singletonMap("message", "Signup Page"));
    }

    /** 회원가입 처리 */
    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> signup(@RequestBody MemberSignUpRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // DTO에서 데이터를 꺼내서 서비스로 전달
            memberService.signup(
                    request.getUsername(),
                    request.getNickname(),
                    request.getPassword(),
                    request.getPasswordConfirm()
            );

            // 성공 시 200 OK와 JSON 메시지 반환
            response.put("message", "Signup Success");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("error", "Signup Failed");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /** 로그인 */
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, String>> login(@RequestBody MemberLoginRequest request, HttpServletRequest httpRequest) {

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        return ResponseEntity.ok(Collections.singletonMap("message", "Login Success"));
    }

    /** * 마이페이지 (GET) */
    @GetMapping("/mypage")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> welcome(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            response.put("nickname", userDetails.getNickname());
            response.put("username", userDetails.getUsername());
        } else {
            response.put("message", "Guest");
        }

        return ResponseEntity.ok(response);
    }

    /** 회원 탈퇴 */
    @PostMapping("/withdraw")
    @ResponseBody
    public ResponseEntity<Map<String, String>> withdraw(Principal principal) {
        String memberId = principal.getName();
        memberService.deleteMember(memberId);

        // 탈퇴 성공 메시지 반환
        return ResponseEntity.ok(Collections.singletonMap("message", "Withdraw Success"));
    }
}