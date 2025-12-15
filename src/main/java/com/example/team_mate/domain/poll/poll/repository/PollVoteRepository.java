package com.example.team_mate.domain.poll.poll.repository;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.poll.poll.entity.Poll;
import com.example.team_mate.domain.poll.poll.entity.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollVoteRepository extends JpaRepository<PollVote, Long> {
    // "멤버"와 "투표" 정보를 가지고 이미 존재하는지(true/false) 확인
    boolean existsByMemberAndPoll(Member member, Poll poll);
}