package com.example.team_mate.domain.schedule.schedule.repository;

import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.schedule.schedule.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    // [start <= rangeEnd] && [end >= rangeStart] 인 이벤트들 = 기간이 겹치는 것들
    List<Event> findByProjectAndEndDateTimeGreaterThanEqualAndStartDateTimeLessThanEqual(
            Project project,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd
    );

    Optional<Event> findByIdAndProject(Long id, Project project);
}