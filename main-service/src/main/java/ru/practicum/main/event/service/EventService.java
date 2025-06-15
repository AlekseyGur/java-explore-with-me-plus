package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public interface EventService {
    EventDto get(Long eventId);

    List<EventDto> get(List<Long> eventIds);

    Page<EventShortDto> getByFilter(EventFilter filter);

    void increaseViews(Long id);

    boolean existsById(Long id);

    EventDto create(Long userId, NewEventDto newEventDto);

    EventDto getByEventIdAndUserId(Long eventId, Long userId);

    Page<EventShortDto> getByUserId(Long userId, Pageable pageable);

    EventDto updateAdmin(Long eventId, UpdateEventDto updated);

    EventDto updateUser(Long eventId, Long userId, UpdateEventDto updated);

    boolean existsByIdAndInitiatorId(Long eventId, Long userId);

    EventRequestStatusUpdateResult updateRequestsStatus(Long eventId, Long userId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<ParticipationRequestDto> findAllRequestsByEventId(Long eventId, Long userId);

}
