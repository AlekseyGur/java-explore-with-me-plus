package ru.practicum.main.event.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.Collection;
import java.util.List;

public interface EventService {
    EventDto get(Long eventId);

    List<EventDto> get(List<Long> eventIds);

    //

    Collection<EventShortDto> getByFilter(EventFilter param);

    void changeViews(Long id);

    @Transactional
    EventDto create(Long userId, NewEventDto eventDto);

    EventDto getEventById(Long userId, Integer eventId);

    Collection<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);

    @Transactional
    EventDto updateEvent(Long userId, Integer eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventDto> search(EventSearchParameters parameters);

    EventDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<ParticipationRequestDto> findRequestsByEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(Long userId,
                    Long eventId,
            EventRequestStatusUpdateRequest request);

    Collection<ParticipationRequestDto> findAllRequestsByEventId(Long userId, Long eventId);
}
