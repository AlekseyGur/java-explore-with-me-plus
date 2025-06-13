package ru.practicum.main.event.service;

public interface EventServiceHelper {
    boolean existsById(Long id);

    boolean checkEventsExistInCategory(Long categoryId);
}
