package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.NewHitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatService statService;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody @Valid NewHitDto newHitDto) {
        log.info("hit: {}", newHitDto);
        statService.save(new HitDto(newHitDto.getApp(), newHitDto.getUri(), newHitDto.getIp(), newHitDto.getTimestamp()));
    }

    @GetMapping(path = "/stats")
    public List<StatDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                  @RequestParam List<String> uris,
                                  @RequestParam boolean unique) {
        log.info("Stats request with params: start: {}, end: {}, uris: {}, unique: {}", start, end, uris, unique);
        return statService.get(start, end, uris, unique);
    }
}