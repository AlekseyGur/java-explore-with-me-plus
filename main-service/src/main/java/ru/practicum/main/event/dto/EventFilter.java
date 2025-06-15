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
@ToString
public class EventFilter {
    private String text;

    private List<Long> categories;

    private Boolean paid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

    @Pattern(regexp = "EVENT_DATE|VIEWS")
    private String sort;

    @PositiveOrZero
    private Integer from;

    @PositiveOrZero
    private Integer size;

    private Boolean onlyAvailable;

    private List<String> states;

    private List<Long> users;

    @Builder.Default
    private Boolean isDtoForAdminApi = false;
}