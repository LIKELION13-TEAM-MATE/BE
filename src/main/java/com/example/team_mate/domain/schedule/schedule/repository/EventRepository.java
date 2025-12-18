package com.example.team_mate.domain.schedule.schedule.repository;

import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.schedule.schedule.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // 내 일정(내가 작성자 OR 참여자)만 조회하는 쿼리
    @Query("SELECT DISTINCT e FROM Event e " +
            "LEFT JOIN e.participants ep " +
            "WHERE (e.createdBy.id = :memberId OR ep.member.id = :memberId) " + // 프로젝트 ID 조건 삭제됨
            "AND e.endDateTime >= :rangeStart " +
            "AND e.startDateTime <= :rangeEnd")
    List<Event> findGlobalMyEvents(
            @Param("memberId") Long memberId,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd
    );

}