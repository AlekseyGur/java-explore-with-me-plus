package ru.practicum.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.service.model.EndpointHit;
import ru.practicum.service.model.ViewStats;
import ru.practicum.service.repository.StatRepository;
import ru.practicum.service.repository.EndpointHitRepository;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final EndpointHitRepository endpointHitRepository;
    private final StatRepository statRepository;

    @Override
    @Transactional
    public void save(HitDto hit) {
        EndpointHit endpointHit = EndpointHit.builder()
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp())
                .build();

        endpointHitRepository.save(endpointHit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<ViewStats> stats;

        if (uris != null && !uris.isEmpty()) {
            stats = unique ? statRepository.getUniqueStatsByUris(start, end, uris)
                    : statRepository.getStatsByUris(start, end, uris);
        } else {
            stats = unique ? statRepository.getUniqueStats(start, end) : statRepository.getStats(start, end);
        }

        return stats.stream()
                .map(stat -> StatDto.builder()
                        .app(stat.getApp())
                        .uri(stat.getUri())
                        .hits(stat.getHits())
                        .build())
                .collect(Collectors.toList());
    }

}