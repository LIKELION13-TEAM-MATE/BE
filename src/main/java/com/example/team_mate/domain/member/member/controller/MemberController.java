package com.example.team_mate.domain.member.member.controller;

import com.example.team_mate.domain.member.member.service.MemberService;
import com.example.team_mate.config.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원가입 페이지
    @GetMapping("/signup")
    public String signupForm() {
        return "member/signup";
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String nickname,
                         @RequestParam String password,
                         @RequestParam String passwordConfirm,
                         Model model) {
        try {
            memberService.signup(username, nickname, password, passwordConfirm);
            return "redirect:/member/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/signup";
        }
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    // welcome 페이지
    @GetMapping("/welcome")
    public String welcome(Model model, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        model.addAttribute("nickname", userDetails.getNickname());
        return "member/welcome"; // templates/member/welcome.html
    }
}
