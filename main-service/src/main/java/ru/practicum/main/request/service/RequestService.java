package ru.practicum.main.request.service;

import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

public interface RequestService {

    List<ParticipationRequestDto> getUserRequests(Long userId);

    List<ParticipationRequestDto> getByEventId(Long eventId);

    ParticipationRequestDto createRequest(Long userId, Long eventId, EventDto event);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    Map<Long, Long> getConfirmedEventsRequestsCount(List<Long> eventsIds);

    List<ParticipationRequestDto> findByEventIdAndIdIn(Long eventId, List<Long> requestsId);

    void setStatusAll(List<Long> ids, String status);
}
