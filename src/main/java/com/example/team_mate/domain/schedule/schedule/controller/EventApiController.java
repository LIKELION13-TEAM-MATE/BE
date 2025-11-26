package com.example.team_mate.domain.schedule.schedule.controller;

import com.example.team_mate.domain.role.role.dto.EventDetailResponse;
import com.example.team_mate.domain.schedule.schedule.dto.EventCreateRequest;
import com.example.team_mate.domain.schedule.schedule.dto.EventDailyResponse;
import com.example.team_mate.domain.schedule.schedule.dto.EventMonthlyDotResponse;
import com.example.team_mate.domain.schedule.schedule.dto.EventUpdateRequest;
import com.example.team_mate.domain.schedule.schedule.entity.Event;
import com.example.team_mate.domain.schedule.schedule.repository.EventRepository;
import com.example.team_mate.domain.schedule.schedule.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Schedule", description = "프로젝트 일정(캘린더) API")
@RequestMapping("/api/v1/projects/{projectId}/events")
public class EventApiController {

    private final EventService eventService;
    private final EventRepository eventRepository;

    // 월별 점(도트) 조회
    @GetMapping("/month")
    @Operation(
            summary = "월별 일정 점 정보 조회",
            description = "해당 프로젝트의 특정 연·월 기준으로, 일정이 있는 날짜에 표시할 점 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = EventMonthlyDotResponse.class))
                    )
            )
    })

    public ResponseEntity<List<EventMonthlyDotResponse>> getMonthlyEvents(
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId,
            @Parameter(description = "연도", example = "2025")
            @RequestParam int year,
            @Parameter(description = "월(1~12)", example = "11")
            @RequestParam int month,
            Authentication authentication
    ) {
        String username = authentication.getName();
        List<EventMonthlyDotResponse> dots =
                eventService.getMonthlyEvents(projectId, username, year, month);
        return ResponseEntity.ok(dots);
    }

    // 일별 일정 리스트 조회
    @GetMapping("/day")
    @Operation(
            summary = "일별 일정 목록 조회",
            description = "캘린더에서 특정 날짜를 눌렀을 때, 그 날의 일정 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = EventDailyResponse.class))
                    )
            )
    })

    public ResponseEntity<List<EventDailyResponse>> getDailyEvents(
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId,
            @Parameter(description = "조회할 날짜(YYYY-MM-DD)", example = "2025-11-26")
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            Authentication authentication
    ) {
        String username = authentication.getName();
        List<EventDailyResponse> events =
                eventService.getDailyEvents(projectId, username, date);
        return ResponseEntity.ok(events);
    }

    // 일정 생성
    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    @Operation(
            summary = "일정 생성",
            description = "프로젝트에 새로운 일정을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "생성 성공(생성된 일정 ID 반환)",
                    content = @Content(schema = @Schema(implementation = Long.class))
            )
    })
    public ResponseEntity<Long> createEvent(
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId,
            @RequestBody EventCreateRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Long eventId = eventService.createEvent(projectId, username, request);
        return ResponseEntity.ok(eventId);
    }

    // 일정 상세 조회
    @GetMapping("/{eventId}")
    @Operation(summary = "일정 상세 조회", description = "일정 카드 클릭 시 해당 일정의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = EventDetailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "일정을 찾을 수 없습니다.",
                    content = @Content // 바디 없음
            )
    })
    public ResponseEntity<?> getEventDetail(
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId,
            @Parameter(description = "일정 ID", example = "10")
            @PathVariable Long eventId
    ) {
        Event event = eventRepository.findById(eventId)
                .orElse(null);

        if (event == null || !event.getProject().getId().equals(projectId)) {
            return ResponseEntity.status(404).build();
        }

        EventDetailResponse response = new EventDetailResponse(
                event.getId(),
                event.getTitle(),
                event.getMemo(),
                event.getStartDateTime(),
                event.getEndDateTime(),
                event.isAllDay(),
                event.getRepeatType(),
                event.getAlarmOffsetMinutes(),
                event.getCreatedBy().getNickname(),
                event.getParticipants().stream()
                        .map(p -> p.getMember().getNickname())
                        .toList(),
                event.isVisibleToParticipantsOnly()
        );

        return ResponseEntity.ok(response);
    }

    // 일정 수정
    @PutMapping(
            value = "/{eventId}",
            consumes = "application/json",
            produces = "application/json"
    )
    @Operation(
            summary = "일정 수정",
            description = "기존에 생성된 일정을 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "일정을 찾을 수 없습니다.")
    })
    public ResponseEntity<Void> updateEvent(
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId,
            @Parameter(description = "일정 ID", example = "10")
            @PathVariable Long eventId,
            @RequestBody EventUpdateRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        eventService.updateEvent(projectId, eventId, username, request);
        return ResponseEntity.ok().build();
    }

    // 일정 삭제
    @DeleteMapping("/{eventId}")
    @Operation(
            summary = "일정 삭제",
            description = "특정 일정을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "일정을 찾을 수 없습니다.")
    })
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "프로젝트 ID", example = "1")
            @PathVariable Long projectId,
            @Parameter(description = "일정 ID", example = "10")
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        eventService.deleteEvent(projectId, eventId, username);
        return ResponseEntity.noContent().build();
    }
}
