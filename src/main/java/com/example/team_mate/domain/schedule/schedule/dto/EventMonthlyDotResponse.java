package com.example.team_mate.domain.schedule.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class EventMonthlyDotResponse {

    private LocalDate date;
    private boolean hasEvent; // 달력에 점(dot) 호출
}
