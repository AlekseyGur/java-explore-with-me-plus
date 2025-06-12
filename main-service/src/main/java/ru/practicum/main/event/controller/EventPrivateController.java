package ru.practicum.main.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.service.EventService;
// import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.Collection;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@PathVariable Long userId,
            @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Creating event by user with id {} - Start", userId);
        return eventService.create(userId, newEventDto);
    }

    @GetMapping
    public Collection<EventShortDto> getEventsByUser(@PathVariable Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting events for user id {} - Start", userId);
        return eventService.getEventsByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable Long userId,
            @PathVariable Integer eventId) {
        log.info("Getting event id {} by user id {} - Start", eventId, userId);
        return eventService.getEventById(userId, eventId);

    }

    @PatchMapping("/{eventId}")
    public EventDto updateEventByUser(@PathVariable Long userId,
            @PathVariable Integer eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Updating event id {} by user id {} - Start", eventId, userId);
        return eventService.update(userId, eventId, updateEventUserRequest);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(@PathVariable Integer userId,
            @PathVariable Integer eventId,
            @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return eventService.updateRequestsStatus(userId, eventId,
                eventRequestStatusUpdateRequest);
    }

    @GetMapping("/{eventId}/requests")
    public Collection<ParticipationRequestDto> getRequestsByOwnerOfEvent(@PathVariable Integer userId,
            @PathVariable Integer eventId) {
        return eventService.findAllRequestsByEventId(userId, eventId);
    }

}