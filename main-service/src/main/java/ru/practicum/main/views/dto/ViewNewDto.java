package ru.practicum.main.views.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
public class ViewNewDto {
    @NotNull
    @PositiveOrZero
    private Long eventId;

    @NotBlank
    @Size(min = 7, max = 15)
    private String ip;
}
