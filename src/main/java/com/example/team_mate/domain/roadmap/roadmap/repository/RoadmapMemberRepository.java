package com.example.team_mate.domain.roadmap.roadmap.repository;

import com.example.team_mate.domain.roadmap.roadmap.entity.RoadmapMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapMemberRepository extends JpaRepository<RoadmapMember, Long> {

}