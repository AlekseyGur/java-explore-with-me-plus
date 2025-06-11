package ru.practicum.main.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {
    @NotNull
    @Positive
    private Long id;

    @Size(min = 2, max = 255)
    private String name;

    @Email
    @Size(min = 2, max = 255)
    private String email;
}
