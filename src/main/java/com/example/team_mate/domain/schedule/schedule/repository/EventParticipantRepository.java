package com.example.team_mate.domain.schedule.schedule.repository;

import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.schedule.schedule.entity.Event;
import com.example.team_mate.domain.schedule.schedule.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {

    List<EventParticipant> findByEvent(Event event);

    boolean existsByEventAndMember(Event event, Member member);
}
