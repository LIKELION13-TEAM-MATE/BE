package com.example.team_mate.domain.poll.poll.repository;

import com.example.team_mate.domain.poll.poll.entity.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollOptionRepository extends JpaRepository<PollOption, Long> {
}