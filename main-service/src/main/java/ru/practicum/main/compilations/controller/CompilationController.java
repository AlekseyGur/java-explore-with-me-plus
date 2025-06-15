package ru.practicum.main.compilations.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.compilations.dto.RequestCompilationCreate;
import ru.practicum.main.compilations.dto.RequestCompilationUpdate;
import ru.practicum.main.compilations.service.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping(path = "/compilations")
    public List<CompilationDto> getList(@RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return compilationService.getCompilationList(pinned, from, size);
    }

    @GetMapping(path = "/compilations/{compId}")
    public CompilationDto getById(@PathVariable @Positive Long compId) {
        return compilationService.getById(compId);
    }

    @PostMapping(path = "/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto post(@Valid @RequestBody RequestCompilationCreate requestDto) {
        return compilationService.createCompilation(requestDto);
    }

    @DeleteMapping(path = "/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping(path = "/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto patch(@PathVariable @Positive Long compId,
            @Valid @RequestBody RequestCompilationUpdate requestDto) {
        return compilationService.updateCompilation(compId, requestDto);
    }
}
