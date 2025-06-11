package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.*;

import java.util.List;

public interface EventService {
    EventDto get(Long eventId);

    List<EventDto> get(List<Long> eventIds);

    List<EventShortDto> getByFilter(EventFilter filter);
}
