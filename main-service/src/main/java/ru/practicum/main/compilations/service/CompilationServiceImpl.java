package ru.practicum.main.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.compilations.dto.RequestCompilationCreate;
import ru.practicum.main.compilations.dto.RequestCompilationUpdate;
import ru.practicum.main.compilations.model.Compilation;
import ru.practicum.main.compilations.model.CompilationMapper;
import ru.practicum.main.compilations.repository.CompilationRepository;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.system.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilationList(boolean pinned, int from, int size) {
        return List.of();
    }

    @Override
    public CompilationDto getById(long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена"));
        return CompilationMapper.toDto(compilation);
    }

    @Override
    public CompilationDto createCompilation(RequestCompilationCreate requestDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(requestDto.getTitle());
        compilation.setPinned(Optional.ofNullable(requestDto.getPinned()).orElse(false));
        if (requestDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(requestDto.getEvents()));
        } else {
            compilation.setEvents(new ArrayList<>());
        }
        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilation(long compId) {

    }

    @Override
    public CompilationDto updateCompilation(long compId, RequestCompilationUpdate requestDto)  {
        return null;
    }
}
