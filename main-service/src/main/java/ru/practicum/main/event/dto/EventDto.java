package ru.practicum.main.event.dto;

import lombok.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.location.dto.LocationDto;
import ru.practicum.main.user.dto.UserDto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EventDto {
    private Long id;

    private String annotation;

    private Long categoryId;

    private CategoryDto category;

    private Long confirmedRequests;

    private LocalDateTime createdOn;

    private LocalDateTime publishedOn;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Long initiatorId;

    private UserDto initiator;

    private LocationDto location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration;

    private String state;

    private String title;

    private Long views;
}
