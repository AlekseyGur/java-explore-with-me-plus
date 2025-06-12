package ru.practicum.main.compilations.service;

import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.compilations.dto.RequestCompilationCreate;
import ru.practicum.main.compilations.dto.RequestCompilationUpdate;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getCompilationList(Boolean pinned, int from, int size);

    CompilationDto getById(long id);

    CompilationDto createCompilation(RequestCompilationCreate requestDto);

    void deleteCompilation(long compId);

    CompilationDto updateCompilation(long compId, RequestCompilationUpdate requestDto);
}
