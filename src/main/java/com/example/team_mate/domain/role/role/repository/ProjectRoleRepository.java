package com.example.team_mate.domain.role.role.repository;

import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.role.role.entity.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRoleRepository extends JpaRepository<ProjectRole, Long> {

    // 한 프로젝트에 속한 역할들
    List<ProjectRole> findByProjectOrderByIdAsc(Project project);
    List<ProjectRole> findByProjectId(Long projectId);
}
