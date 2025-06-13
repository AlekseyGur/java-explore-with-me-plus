package ru.practicum.main.event.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.main.event.repository.EventRepository;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServiceHelperImpl implements EventServiceHelper {

    private final EventRepository eventRepository;

    @Override
    public boolean existsById(Long id) {
        return eventRepository.existsById(id);
    }

    @Override
    public boolean checkEventsExistInCategory(Long categoryId) {
        return eventRepository.existsByCategoryId(categoryId);
    }
}
