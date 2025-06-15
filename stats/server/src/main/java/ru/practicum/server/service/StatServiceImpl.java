package ru.practicum.server.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.repository.EndpointHitRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {
    private final EndpointHitRepository endpointHitRepository;

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
        endpointHitRepository.flush();
    }

    @Override
    public List<StatDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала должна быть позже даты окончания");
        }

        if (unique) {
            return endpointHitRepository.findHitsUnique(start, end, uris).stream().map(x -> StatDto.builder()
                    .app(x[0].toString())
                    .uri(x[1].toString())
                    .hits((Long) x[2]).build()).toList();
        }

        return endpointHitRepository.findHits(start, end, uris).stream().map(x -> StatDto.builder()
                .app(x[0].toString())
                .uri(x[1].toString())
                .hits((Long) x[2]).build()).toList();

    }

}