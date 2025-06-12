package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.main.location.dto.LocationUpdateDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventDto {
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationUpdateDto location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Size(min = 3, max = 120)
    private String title;
}