package com.example.team_mate.domain.project.project.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ProjectCreateRequest {
    private String projectName;
    private String category;
    private LocalDate deadline;
    private String themeColor;
}