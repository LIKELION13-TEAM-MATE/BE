package com.example.team_mate.domain.file.file.repository;

import com.example.team_mate.domain.file.file.entity.AttachedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachedFileRepository extends JpaRepository<AttachedFile, Long> {
}