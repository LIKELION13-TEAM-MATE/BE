package com.example.team_mate.domain.home.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home/first"; // 로그인 or 회원가입 화면
    }

    @GetMapping("/home/main")
    public String mainPage() {
        return "home/main"; // 메인화면(프엔 작업 시작 부분)
    }
}

