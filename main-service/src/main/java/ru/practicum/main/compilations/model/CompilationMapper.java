package ru.practicum.main.compilations.model;

import java.util.List;

import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.mapper.EventMapper;

public class CompilationMapper {
    public static CompilationDto toDto(Compilation compilation) {
        List<EventShortDto> events = compilation.getEvents().stream()
                .map(EventMapper::toShortDto).toList();

        return new CompilationDto(events,
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle());
    }
}
