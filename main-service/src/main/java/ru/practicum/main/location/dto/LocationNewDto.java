package ru.practicum.main.location.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class LocationNewDto {
    @NotNull(message = "Широта не может быть пустой")
    @DecimalMin(value = "-90.00000000", message = "Широта должна быть от -90 до 90")
    @DecimalMax(value = "90.00000000", message = "Широта должна быть от -90 до 90")
    private Float lat;

    @NotNull(message = "Долгота не может быть пустой")
    @DecimalMin(value = "-180.00000000", message = "Долгота должна быть от -180 до 180")
    @DecimalMax(value = "180.00000000", message = "Долгота должна быть от -180 до 180")
    private Float lon;
}
