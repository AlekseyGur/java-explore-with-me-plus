package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    EventDto get(Long eventId);

    List<EventDto> get(List<Long> eventIds);

    Page<EventShortDto> getByFilter(EventFilter filter);

    void increaseViews(Long id);

    boolean existsById(Long id);

    EventDto create(Long userId, NewEventDto newEventDto);

    EventDto getByEventIdAndUserId(Long userId, Long eventId);

    Page<EventShortDto> getByUserId(Long userId, Pageable pageable);
}
