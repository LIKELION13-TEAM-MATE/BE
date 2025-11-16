package com.example.team_mate.domain.member.member.service;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    public Member signup(String username, String nickname, String password, String passwordConfirm) {

        if (!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (memberRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (password.length() < 8 ||
                !password.matches(".*[0-9].*") ||
                !password.matches(".*[!@#$%^&*()].*")) {
            throw new IllegalArgumentException("비밀번호는 8자 이상 / 숫자, 특수문자를 포함해야 합니다.");
        }

        Member member = new Member();
        member.setUsername(username);
        member.setNickname(nickname);
        member.setPassword(passwordEncoder.encode(password)); // 암호화

        return memberRepository.save(member);
    }

    // 로그인
    public Member login(String username, String password) {
        return memberRepository.findByUsername(username)
                .filter(m -> passwordEncoder.matches(password, m.getPassword()))
                .orElse(null);
    }
}
