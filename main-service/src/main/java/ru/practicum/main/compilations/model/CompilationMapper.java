package ru.practicum.main.compilations.model;

import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.event.mapper.EventMapper;

public class CompilationMapper {
    public static CompilationDto toDto(Compilation compilation) {
        return new CompilationDto(compilation.getEvents().stream()
                .map(EventMapper::toShortDto).toList(),
                                        compilation.getId(),
                                compilation.getPinned(),
                                compilation.getTitle());
    }
}
