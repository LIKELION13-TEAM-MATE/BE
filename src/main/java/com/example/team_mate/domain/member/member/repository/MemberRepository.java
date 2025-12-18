package com.example.team_mate.domain.member.member.repository;

import com.example.team_mate.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 로그인 및 유저 조회용
    Optional<Member> findByUsername(String username);

    // (회원가입 시) 아이디 중복 체크용
    boolean existsByUsername(String username);

    // 아이디 찾아서 삭제하는 메서드
    void deleteByUsername(String username);
}