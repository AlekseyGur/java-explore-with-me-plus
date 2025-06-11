package ru.practicum.main.request.service;

import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

public interface RequestService {

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    Map<Long, Integer> getConfirmedEventsRequestsCount(List<Long> eventsIds);

}
