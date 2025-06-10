package ru.practicum.server.service;

import java.util.List;
import java.time.LocalDateTime;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;

public interface StatService {
    void save(HitDto hit);

    List<StatDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}