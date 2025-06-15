package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    EventDto get(Long eventId);

    EventDto getPublic(Long eventId);

    List<EventDto> get(List<Long> eventIds);

    EventDto getPublished(Long eventId);

    List<EventDto> getPublished(List<Long> eventIds);

    List<EventShortDto> getByFilter(EventFilter filter);

    void increaseViews(Long eventId, String ip);

    boolean existsById(Long id);

    boolean checkEventsExistInCategory(Long categoryId);

    EventDto create(Long userId, NewEventDto newEventDto);

    EventDto getByEventIdAndUserId(Long eventId, Long userId);

    Page<EventShortDto> getByUserId(Long userId, Pageable pageable);

    EventDto updateByAdmin(Long eventId, UpdateEventDto updated);

    EventDto updateByUser(Long eventId, Long userId, UpdateEventDto updated);

    boolean existsByIdAndInitiatorId(Long eventId, Long userId);

    EventRequestStatusUpdateResult updateRequestsStatus(Long eventId, Long userId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<ParticipationRequestDto> findAllRequestsByEventId(Long eventId, Long userId);
}
