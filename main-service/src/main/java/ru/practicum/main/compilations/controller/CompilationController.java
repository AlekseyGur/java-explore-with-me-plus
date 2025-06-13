package ru.practicum.main.compilations.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.compilations.dto.RequestCompilationCreate;
import ru.practicum.main.compilations.dto.RequestCompilationUpdate;
import ru.practicum.main.compilations.service.CompilationService;

import java.util.List;

@Slf4j
@RestController
// @RequestMapping(path = "/admin")
@Validated
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping(path="/compilations")
    public List<CompilationDto> getList(@RequestParam(required = false) Boolean pinned,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        log.info("Запрос на получение подборки событий");
        return compilationService.getCompilationList(pinned, from, size);
    }

    @GetMapping(path="/compilations/{compId}")
    public CompilationDto getById(@PathVariable long compId) {
        log.info("Запрос на получение подборки событий по Id");
        return compilationService.getById(compId);
    }

    @PostMapping(path = "/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto post(@Valid @RequestBody RequestCompilationCreate requestDto) {
        log.info("Запрос на создание подборки событий {}", requestDto);
        return compilationService.createCompilation(requestDto);
    }

    @DeleteMapping(path = "/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long compId) {
        log.info("Запрос на удаление подборки событий с Id: {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping(path = "/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto patch(@PathVariable long compId, @Valid @RequestBody RequestCompilationUpdate requestDto) {
        log.info("PATCH Обновление подборки событий {}", requestDto);
        return compilationService.updateCompilation(compId, requestDto);
    }
}
