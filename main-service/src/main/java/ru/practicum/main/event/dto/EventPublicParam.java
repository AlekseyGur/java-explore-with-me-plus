package ru.practicum.main.event.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Collection;

@AllArgsConstructor
@Getter
@Setter
public class EventPublicParam {
    @NotNull
    private String text;

    private Collection<Long> categories;

    private Boolean paid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

    private Boolean onlyAvailable;

    @Pattern(regexp = "EVENT_DATE|VIEWS")
    private String sort;

    @PositiveOrZero
    private Long from;

    @PositiveOrZero
    private Long size;
}