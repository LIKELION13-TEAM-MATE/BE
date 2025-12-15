package com.example.team_mate.domain.schedule.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Schema(description = "월별 캘린더 점 표시용 응답 DTO")
public class EventMonthlyDotResponse {

    @Schema(description = "날짜", example = "2025-11-24")
    private LocalDate date;

    @Schema(description = "해당 날짜에 일정이 하나라도 있는지 여부", example = "true")
    private boolean hasEvent;

    @Schema(description = "해당 날짜의 일정 개수", example = "3")
    private int eventCount;

    public static EventMonthlyDotResponse of(LocalDate date, int eventCount) {
        boolean hasEvent = eventCount > 0;
        return new EventMonthlyDotResponse(date, hasEvent, eventCount);
    }
}