package com.example.team_mate.domain.schedule.schedule.controller;

import com.example.team_mate.domain.schedule.schedule.dto.EventDailyResponse;
import com.example.team_mate.domain.schedule.schedule.dto.EventMonthlyDotResponse;
import com.example.team_mate.domain.schedule.schedule.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class GlobalEventController {

    private final EventService eventService;

    /** 일정 탭 메인 – 달력 화면 */
    @GetMapping("/my-calendar")
    @ResponseBody
    public ResponseEntity<Map<String, String>> showGlobalCalendarPage() {
        // HTML 뷰 대신 간단한 JSON 응답 반환
        return ResponseEntity.ok(Collections.singletonMap("message", "Global Calendar Page"));
    }

    /** 월별 이벤트 점 정보 (Ajax) */
    @GetMapping("/api/events/month")
    @ResponseBody
    public ResponseEntity<List<EventMonthlyDotResponse>> getGlobalMonthly(
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                eventService.getGlobalMonthlyEvents(authentication.getName(), year, month)
        );
    }

    /** 일별 일정 리스트 (Ajax) */
    @GetMapping("/api/events/day")
    @ResponseBody
    public ResponseEntity<List<EventDailyResponse>> getGlobalDaily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                eventService.getGlobalDailyEvents(authentication.getName(), date)
        );
    }
}