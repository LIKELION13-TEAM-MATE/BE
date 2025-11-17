package com.example.team_mate.domain.schedule.schedule.dto;

import com.example.team_mate.domain.schedule.schedule.entity.RepeatType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventUpdateRequest {

    private String title;
    private String memo;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private boolean allDay;

    private RepeatType repeatType = RepeatType.NONE;

    private Integer alarmOffsetMinutes;

    private List<Long> participantIds;

    private boolean visibleToParticipantsOnly;
}
