package ru.practicum.main.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
public class CategoryUpdateDto {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 50, message = "Название категории не должно превышать 50 символов")
    private String name;
}
