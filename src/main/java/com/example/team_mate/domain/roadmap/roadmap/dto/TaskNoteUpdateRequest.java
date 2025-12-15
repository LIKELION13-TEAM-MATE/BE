package com.example.team_mate.domain.roadmap.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Task 메모 수정 요청 DTO")
public class TaskNoteUpdateRequest {
    @Schema(description = "메모 내용", example = "팀프로젝트 관리 앱 서비스")
    private String note;
}
