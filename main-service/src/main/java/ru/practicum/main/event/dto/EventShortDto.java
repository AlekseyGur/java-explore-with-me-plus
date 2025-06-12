package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventShortDto {
    private Long id;

    private String annotation;

    private Long categoryId;

    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Long initiatorId;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;
}
