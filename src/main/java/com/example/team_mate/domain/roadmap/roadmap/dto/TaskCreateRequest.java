package com.example.team_mate.domain.roadmap.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Task 생성 요청 DTO")
public class TaskCreateRequest {

    @Schema(description = "Task 내용", example = "주제 확정")
    private String content;
}