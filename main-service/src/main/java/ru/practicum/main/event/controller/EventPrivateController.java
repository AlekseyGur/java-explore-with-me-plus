package ru.practicum.main.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@PathVariable @Positive Long userId,
                    @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.create(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(from, size);
        return eventService.getByUserId(userId, pageable).getContent();
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable Long userId,
            @PathVariable Long eventId) {
        return eventService.getByEventIdAndUserId(eventId, userId);

    }

    @PatchMapping("/{eventId}")
    public EventDto updateEventByUser(
                    @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventDto updateEventUserRequest) {
        return eventService.updateByUser(eventId, userId, updateEventUserRequest);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(
            @PathVariable Long userId,
                    @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest req,
            HttpServletRequest request) {
        return eventService.updateRequestsStatus(eventId, userId, req);
    }

    @GetMapping("/{eventId}/requests")
    public Collection<ParticipationRequestDto> getRequestsByOwnerOfEvent(@PathVariable Long userId,
            @PathVariable Long eventId) {
        return eventService.findAllRequestsByEventId(eventId, userId);
    }
}