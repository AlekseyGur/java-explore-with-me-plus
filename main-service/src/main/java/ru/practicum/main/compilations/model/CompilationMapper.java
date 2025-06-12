package ru.practicum.main.compilations.model;

import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.event.model.MapperEvent;

public class CompilationMapper {
    public static CompilationDto toDto(Compilation compilation) {
        return new CompilationDto(compilation.getEvents().stream()
                                        .map(MapperEvent::toEventShortDto).toList(),
                                compilation.getId(),
                                compilation.getPinned(),
                                compilation.getTitle());
    }
}
