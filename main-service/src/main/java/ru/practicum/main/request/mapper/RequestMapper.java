package ru.practicum.main.request.mapper;

import java.util.List;

import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.model.ParticipationRequest;

public class RequestMapper {
    public static ParticipationRequestDto toDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEventId())
                .requester(request.getRequesterId())
                .status(request.getStatus())
                .build();
    }

    public static List<ParticipationRequestDto> toDto(List<ParticipationRequest> events) {
        return events.stream().map(RequestMapper::toDto).toList();
    }
}
