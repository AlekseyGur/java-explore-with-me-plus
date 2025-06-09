package ru.practicum.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.service.model.EndpointHit;
import ru.practicum.service.model.ViewStats;
import ru.practicum.service.repository.EndpointHitRepository;
import ru.practicum.service.repository.StatRepository;

@SpringBootTest
@ContextConfiguration(classes = StatConfig.class)
@ExtendWith(MockitoExtension.class)
class StatServiceTest {

    @Mock
    private EndpointHitRepository endpointHitRepository;

    @Mock
    private StatRepository statRepository;

    @InjectMocks
    private StatServiceImpl statService;

    private HitDto hitDto;
    private ViewStats viewStats;

    @BeforeEach
    void setUp() {
        hitDto = new HitDto("appName", "/some/endpoint", "127.0.0.1", LocalDateTime.now());
        viewStats = new ViewStats("appName", "/some/endpoint", 1L);
        this.statService = new StatServiceImpl(endpointHitRepository, statRepository);
    }

    @Test
    void testSaveHit() {
        statService.save(hitDto);
        verify(endpointHitRepository, times(1)).save(any(EndpointHit.class));
    }

    @Test
    void testGetStatsWithUniqueViews() {
        List<String> uris = Arrays.asList("/some/endpoint");
        boolean unique = true;
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        when(statRepository.getUniqueStatsByUris(start, end, uris)).thenReturn(Arrays.asList(viewStats));

        List<StatDto> result = statService.get(start, end, uris, unique);

        assertEquals(1, result.size(), "Количество результатов должно быть равно 1");
        verify(statRepository, times(1)).getUniqueStatsByUris(start, end, uris);
    }

    @Test
    void testGetStatsWithoutUniqueViews() {
        List<String> uris = Collections.emptyList();
        boolean unique = false;
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        when(statRepository.getStats(start, end)).thenReturn(new ArrayList<>());

        List<StatDto> result = statService.get(start, end, uris, unique);

        assertTrue(result.isEmpty(), "Результат должен быть пустой список");
        verify(statRepository, times(1)).getStats(start, end);
    }

    @Test
    void testGetStatsForSpecificUrisAndUniqueIP() {
        List<String> uris = Arrays.asList("/specific/path");
        boolean unique = true;
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now();

        when(statRepository.getUniqueStatsByUris(start, end, uris)).thenReturn(Arrays.asList(viewStats));

        List<StatDto> result = statService.get(start, end, uris, unique);

        assertEquals(1, result.size(), "Количество результатов должно быть равно 1");
        verify(statRepository, times(1)).getUniqueStatsByUris(start, end, uris);
    }

    @Test
    void testGetStatsWhenNoDataFound() {
        List<String> uris = Arrays.asList("/nonexistent/path");
        boolean unique = true;
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now();

        when(statRepository.getUniqueStatsByUris(start, end, uris)).thenReturn(Collections.emptyList());

        List<StatDto> result = statService.get(start, end, uris, unique);

        assertTrue(result.isEmpty(), "Ожидается пустой список результата");
        verify(statRepository, times(1)).getUniqueStatsByUris(start, end, uris);
    }
}