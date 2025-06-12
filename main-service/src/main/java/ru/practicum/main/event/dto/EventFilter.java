package ru.practicum.main.event.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class EventFilter {
    String text;

    List<Long> categories;

    Boolean paid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rangeEnd;

    @Pattern(regexp = "EVENT_DATE|VIEWS")
    String sort;

    @PositiveOrZero
    Integer from;

    @PositiveOrZero
    Integer size;

    Boolean onlyAvailable;

    List<String> states;

    List<Long> users;

    @Builder.Default
    Boolean isDtoForAdminApi = false;
}