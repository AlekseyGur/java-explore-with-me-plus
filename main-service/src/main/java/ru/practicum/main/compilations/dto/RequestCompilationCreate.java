package ru.practicum.main.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class RequestCompilationCreate {
    private List<Long> events;
    private Boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}
