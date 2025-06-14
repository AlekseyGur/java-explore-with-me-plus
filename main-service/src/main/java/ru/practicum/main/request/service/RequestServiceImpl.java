package ru.practicum.main.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.system.exception.NotFoundException;
import ru.practicum.main.system.exception.BadConditionsException;
import ru.practicum.main.system.exception.DuplicatedDataException;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.enums.RequestStatus;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        UserDto user = userService.get(userId);
        return requestRepository.findAllByRequesterId(user.getId()).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getByEventId(Long eventId) {
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId, EventDto event) {
        if (requestRepository.findByEventIdAndRequesterId(eventId, userId).isPresent()) {
            throw new DuplicatedDataException("Заявка уже существует");
        }

        if (event.getInitiatorId().equals(userId)) {
            throw new BadConditionsException("Нельзя подавать заявку на своё собственное событие");
        }

        if (event.getParticipantLimit() != 0 && !event.getState().equals(EventState.PUBLISHED.toString())) {
            throw new BadConditionsException("Нельзя подавать заявку на неопубликованное событие");
        }

        int requestsConfirmed = requestRepository.findAllByEventIdAndStatus(
                eventId,
                RequestStatus.CONFIRMED.toString()).size();

        if (event.getParticipantLimit() > 0 && requestsConfirmed >= event.getParticipantLimit()) {
            throw new BadConditionsException("Слишком много участников");
        }

        if (!event.getRequestModeration() && requestsConfirmed >= event.getParticipantLimit()) {
            throw new DuplicatedDataException("Слишком много участников");
        }

        UserDto user = userService.get(userId);
        ParticipationRequest request = ParticipationRequest.builder()
                .requesterId(user.getId())
                .eventId(event.getId())
                .status(RequestStatus.PENDING.toString())
                .created(LocalDateTime.now())
                .build();

        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED.toString());
        }

        if (event.getParticipantLimit().equals(0L)) {
            request.setStatus(RequestStatus.CONFIRMED.toString());
        }

        ParticipationRequest res = requestRepository.save(request);
        requestRepository.flush();
        return RequestMapper.toDto(res);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена"));

        UserDto user = userService.get(request.getRequesterId());
        if (!user.getId().equals(userId)) {
            throw new BadConditionsException("Нельзя отменить чужую заявку");
        }

        request.setStatus(RequestStatus.CANCELED.toString());

        ParticipationRequestDto res = RequestMapper.toDto(requestRepository.save(request));
        requestRepository.flush();
        return res;
    }

    @Override
    public Map<Long, Long> getConfirmedEventsRequestsCount(List<Long> eventsIds) {
        return requestRepository
                .getCountByEventIdInAndStatus(eventsIds, RequestStatus.CONFIRMED.toString()).stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).longValue()
                ));
    }

    @Override
    public List<ParticipationRequestDto> findByEventIdAndIdIn(Long eventId, List<Long> requestsId) {
        return RequestMapper.toDto(requestRepository.findByEventIdAndIdIn(eventId, requestsId));
    }

    @Override
    @Transactional
    public void setStatusAll(List<Long> ids, String status) {
        for (ParticipationRequest request : requestRepository.findAllByIdIn(ids)) {
            if (request.getStatus().equals(RequestStatus.CONFIRMED.toString())) {
                throw new BadConditionsException("Нельзя отменить подтверждённую заявку");
            }
        }

        requestRepository.setStatusAll(ids, status);
        requestRepository.flush();
    }

}
