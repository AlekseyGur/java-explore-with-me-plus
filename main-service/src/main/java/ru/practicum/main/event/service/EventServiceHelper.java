package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.EventDto;

public interface EventServiceHelper {
    boolean existsById(Long id);

    boolean checkEventsExistInCategory(Long categoryId);

    EventDto getRaw(Long eventId);
}
