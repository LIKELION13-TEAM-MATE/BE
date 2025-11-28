package com.example.team_mate.domain.roadmap.roadmap.repository;

import com.example.team_mate.domain.roadmap.roadmap.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    // 프로젝트 ID로 로드맵 목록 가져오기 (ID순 정렬)
    List<Roadmap> findAllByProjectIdOrderByIdAsc(Long projectId);
}
