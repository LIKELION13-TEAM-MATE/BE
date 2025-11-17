package com.example.team_mate.domain.schedule.schedule.dto;

import com.example.team_mate.domain.schedule.schedule.entity.RepeatType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EventDailyResponse {

    private Long eventId;
    private String title;
    private String memo;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private boolean allDay;
    private RepeatType repeatType;
    private Integer alarmOffsetMinutes;
    private boolean createdByMe;
}
