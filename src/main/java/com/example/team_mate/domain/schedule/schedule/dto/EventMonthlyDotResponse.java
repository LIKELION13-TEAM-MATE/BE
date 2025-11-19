package com.example.team_mate.domain.schedule.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class EventMonthlyDotResponse {

    private LocalDate date;
    private boolean hasEvent;
    private int eventCount;

    public static EventMonthlyDotResponse of(LocalDate date, int eventCount) {
        boolean hasEvent = eventCount > 0;
        return new EventMonthlyDotResponse(date, hasEvent, eventCount);
    }
}