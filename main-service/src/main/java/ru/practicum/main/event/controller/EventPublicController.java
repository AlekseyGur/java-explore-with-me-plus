package ru.practicum.main.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.dto.EventPublicParam;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.service.EventService;
import ru.practicum.client.StatClient;
import ru.practicum.dto.HitDto;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping(path = "/events")
@AllArgsConstructor
@Validated
public class EventPublicController {
        private final StatClient statsClient;
        private final EventService eventService;

        @GetMapping
        @ResponseStatus(HttpStatus.OK)
        public Collection<EventShortDto> find(@RequestParam(required = false) String text,
                        @RequestParam(required = false) Collection<Long> categories,
                        @RequestParam(required = false) Boolean paid,
                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Future LocalDateTime rangeEnd,
                        @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                        @RequestParam(required = false) String sort,
                        @RequestParam(defaultValue = "0") Long from,
                        @RequestParam(defaultValue = "10") Long size,
                        HttpServletRequest request) {

                EventPublicParam param = new EventPublicParam(text, categories, paid,
                                rangeStart, rangeEnd, onlyAvailable, sort, from, size);
                log.info("GET Поиск события по параметрам {}", param);
                HitDto hitDto = HitDto.builder()
                                .app("ewm-main-service")
                                .ip(request.getRemoteAddr())
                                .uri(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .build();

                statsClient.hit(hitDto);
                return eventService.getEventsByFilter(param);
        }

        @GetMapping(path = "/{eventId}")
        @ResponseStatus(HttpStatus.OK)
        public EventDto getById(@PathVariable long eventId, HttpServletRequest request) {
                log.info("GET Поиск события по id {}", eventId);
                HitDto hitDto = HitDto.builder()
                                .app("ewm-main-service")
                                .ip(request.getRemoteAddr())
                                .uri(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .build();

                statsClient.hit(hitDto);
                return eventService.getById(eventId);
        }
}
