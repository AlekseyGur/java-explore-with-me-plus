package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.location.model.Location;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventShortDto {
    private Long id;

    private String description;

    private String annotation;

    private Long categoryId;

    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Location location;

    private Long initiatorId;

    private Long participantLimit;

    private UserShortDto initiator;

    private Boolean paid;

    private Boolean requestModeration;

    private String title;

    private String state;

    private Long views;
}
