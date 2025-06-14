package ru.practicum.main.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.event.dto.EventDto;

import java.util.List;

@UtilityClass
public class EventMapper {

    public static EventDto toDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .categoryId(event.getCategoryId())
                .eventDate(event.getEventDate())
                .initiatorId(event.getInitiatorId())
                .participantLimit(event.getParticipantLimit())
                .confirmedRequests(event.getConfirmedRequests())
                .requestModeration(event.getRequestModeration())
                .paid(event.getPaid())
                .state(event.getState())
                .createdOn(event.getCreatedOn())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .description(event.getDescription())
                .annotation(event.getAnnotation())
                .categoryId(event.getCategoryId())
                .eventDate(event.getEventDate())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .confirmedRequests(event.getConfirmedRequests())
                .participantLimit(event.getParticipantLimit())
                .initiatorId(event.getInitiatorId())
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .state(event.getTitle())
                .requestModeration(event.getRequestModeration())
                .build();
    }

    public static EventShortDto toShortDto(EventDto event) {
        return EventShortDto.builder()
                .id(event.getId())
                .description(event.getDescription())
                .annotation(event.getAnnotation())
                .categoryId(event.getCategoryId())
                .category(event.getCategory())
                .eventDate(event.getEventDate())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .confirmedRequests(event.getConfirmedRequests())
                .participantLimit(event.getParticipantLimit())
                .initiatorId(event.getInitiatorId())
                .initiator(UserMapper.toShortfromDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .state(event.getTitle())
                .requestModeration(event.getRequestModeration())
                .build();
    }

    public static Event fromNew(NewEventDto event) {
        return Event.builder()
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .categoryId(event.getCategory())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .confirmedRequests(0L)
                .requestModeration(event.getRequestModeration())
                .eventDate(event.getEventDate())
                .views(0L)
                .build();
    }

    public static List<EventShortDto> toShortDto(List<Event> events) {
        return events.stream().map(EventMapper::toShortDto).toList();
    }

    public static List<EventShortDto> toShortDtoFromDto(List<EventDto> events) {
        return events.stream().map(EventMapper::toShortDto).toList();
    }
}
