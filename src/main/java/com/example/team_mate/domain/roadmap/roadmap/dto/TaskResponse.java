package com.example.team_mate.domain.roadmap.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Task 응답 DTO")
public class TaskResponse {
    @Schema(description = "Task ID", example = "1")
    private Long id;

    @Schema(description = "Task 내용", example = "주제 확정")
    private String content;

    @Schema(description = "완료 여부", example = "false")
    private boolean checked;

    @Schema(description = "메모 내용", example = "팀프로젝트 관리 앱 서비스")
    private String note;
}
