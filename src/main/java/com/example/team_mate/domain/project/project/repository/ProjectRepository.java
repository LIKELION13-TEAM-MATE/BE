package com.example.team_mate.domain.project.project.repository;

import com.example.team_mate.domain.project.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}