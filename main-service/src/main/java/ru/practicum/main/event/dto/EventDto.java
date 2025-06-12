package ru.practicum.main.event.dto;

import lombok.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.location.dto.LocationDto;
import ru.practicum.main.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDto {
    private Long id;

    private String annotation;

    private Long categoryId;

    private CategoryDto category;

    private Integer confirmedRequests;

    private LocalDateTime createdOn;

    private String publishedOn;

    private String description;

    private LocalDateTime eventDate;

    private Long initiatorId;

    private UserDto initiator;

    private LocationDto location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration;

    private EventState state;

    private String title;

    private Long views;
}
