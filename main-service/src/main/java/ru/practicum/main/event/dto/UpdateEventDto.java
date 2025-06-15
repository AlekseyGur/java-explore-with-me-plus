package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
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
    @Size(min = 3, max = 120)
    private String title;

    @Size(min = 20, max = 2000)
    private String annotation;

    @Size(min = 20, max = 7000)
    private String description;

    private Long category;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationUpdateDto location;

    private Boolean paid;

    @PositiveOrZero
    private Long participantLimit;

    private Boolean requestModeration;

    @Pattern(regexp = "PUBLISH_EVENT|REJECT_EVENT|SEND_TO_REVIEW|CANCEL_REVIEW")
    private String stateAction;

    @Pattern(regexp = "PUBLISH_EVENT|REJECT_EVENT|SEND_TO_REVIEW|CANCEL_REVIEW")
    private String stateActionUser;

}