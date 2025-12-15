package com.example.team_mate.domain.roadmap.roadmap.repository;

import com.example.team_mate.domain.roadmap.roadmap.entity.RoadmapTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapTaskRepository extends JpaRepository<RoadmapTask, Long> {

}