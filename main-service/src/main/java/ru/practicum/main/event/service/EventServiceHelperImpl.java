package ru.practicum.main.event.service;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.system.exception.NotFoundException;

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

    @Override
    public EventDto getRaw(Long eventId) {
        EventDto event = eventRepository.findById(eventId)
                .map(EventMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Событие с таким id не найдено"));
        return event;
    }
}
