package com.example.team_mate.domain.poll.poll.repository;

import com.example.team_mate.domain.poll.poll.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<Poll, Long> {
}