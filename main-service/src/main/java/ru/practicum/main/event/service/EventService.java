package ru.practicum.main.event.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.Collection;
import java.util.List;

public interface EventService {
    EventDto getById(long eventId);

    Collection<EventShortDto> getEventsByFilter(EventPublicParam param);

    void changeViews(Long id);

    @Transactional
    EventDto create(Long userId, NewEventDto eventDto);

    EventDto getEventById(Long userId, Integer eventId);

    Collection<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);

    @Transactional
    EventDto updateEvent(Long userId, Integer eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventDto> search(EventSearchParameters parameters);

    EventDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<ParticipationRequestDto> findRequestsByEventId(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(long userId,
            long eventId,
            EventRequestStatusUpdateRequest request);

    Collection<ParticipationRequestDto> findAllRequestsByEventId(long userId, long eventId);
}
