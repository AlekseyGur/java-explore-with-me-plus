package ru.practicum.main.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.dto.EventFilter;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.service.EventService;
import ru.practicum.client.StatClient;
import ru.practicum.dto.HitDto;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping(path = "/events")
@AllArgsConstructor
@RestController
@Validated
public class EventPublicController {
        private final StatClient statsClient;
        private final EventService eventService;

        @GetMapping
        @ResponseStatus(HttpStatus.OK)
        public List<EventShortDto> find(
                                        @RequestParam(required = false) String text,
                        @RequestParam(required = false) List<Long> categories,
                        @RequestParam(required = false) Boolean paid,
                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                        @RequestParam(required = false) String sort,
                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size,
                                        HttpServletRequest request) {

                HitDto hitDto = HitDto.builder()
                                .app("ewm-main-service")
                                .ip(request.getRemoteAddr())
                                .uri(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .build();
                statsClient.hit(hitDto);

                EventFilter param = EventFilter.builder()
                                .text(text)
                                .categories(categories)
                                .paid(paid)
                                .rangeStart(rangeStart)
                                .rangeEnd(rangeEnd)
                                .onlyAvailable(onlyAvailable)
                                .sort(sort)
                                .from(from)
                                .size(size)
                                .build();

                return eventService.getByFilter(param);
        }

        @GetMapping(path = "/{eventId}")
        @ResponseStatus(HttpStatus.OK)
        public EventDto getById(@PathVariable @Positive Long eventId, HttpServletRequest request) {

                HitDto hitDto = HitDto.builder()
                                .app("ewm-main-service")
                                .ip(request.getRemoteAddr())
                                .uri(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .build();
                statsClient.hit(hitDto);

                eventService.increaseViews(eventId, request.getRemoteAddr());
                return eventService.getPublic(eventId);
        }
}
