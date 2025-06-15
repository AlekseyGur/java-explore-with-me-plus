package ru.practicum.server.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.server.StatServer;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.model.ViewStats;
import ru.practicum.server.repository.EndpointHitRepository;

@SpringBootTest(classes = { StatServer.class })
@ExtendWith(MockitoExtension.class)
public class StatServiceTest {

    @Mock
    private EndpointHitRepository endpointHitRepository;

    @InjectMocks
    private StatServiceImpl statService;

    private HitDto hitDto;
    private ViewStats viewStats;

    @BeforeEach
    void setUp() {
        hitDto = new HitDto("appName", "/some/endpoint", "127.0.0.1", LocalDateTime.now());
        viewStats = new ViewStats("appName", "/some/endpoint", 1L);
        this.statService = new StatServiceImpl(endpointHitRepository);
    }

    @Test
    void testSaveHit() {
        statService.save(hitDto);
        verify(endpointHitRepository, times(1)).save(any(EndpointHit.class));
    }

    @Test
    void testfindByTimestampBetweenWithUniqueViews() {
        List<String> uris = Arrays.asList("/some/endpoint");
        boolean unique = true;
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<Object[]> rawData = Arrays.asList(
                new Object[] { "app1", "/api/v1/events", 2L },
                new Object[] { "app1", "/api/v1/users", 1L },
                new Object[] { "app2", "/api/v1/events", 1L },
                new Object[] { "app2", "/api/v1/users", 1L });

        when(endpointHitRepository.findHitsUnique(start, end, uris)).thenReturn(rawData);

        List<StatDto> result = statService.get(start, end, uris, unique);

        assertEquals(4, result.size(), "Количество результатов должно быть равно 1");
        verify(endpointHitRepository, times(1)).findHitsUnique(start, end, uris);
    }

    @Test
    void testfindByTimestampBetweenWithoutUniqueViews() {
        List<String> uris = Collections.emptyList();
        boolean unique = false;
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<Object[]> rawData = Collections.emptyList();

        when(endpointHitRepository.findHits(start, end, uris)).thenReturn(rawData);

        List<StatDto> result = statService.get(start, end, uris, unique);

        assertTrue(result.isEmpty(), "Результат должен быть пустой список");
        verify(endpointHitRepository, times(1)).findHits(start, end, uris);
    }

    @Test
    void testfindByTimestampBetweenForSpecificUrisAndUniqueIP() {
        List<String> uris = Arrays.asList("/specific/path");
        boolean unique = true;
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now();
        List<Object[]> rawData = Arrays.asList(
                new Object[] { "app1", "/api/v1/events", 2L },
                new Object[] { "app1", "/api/v1/users", 1L },
                new Object[] { "app2", "/api/v1/events", 1L },
                new Object[] { "app2", "/api/v1/users", 1L });

        when(endpointHitRepository.findHitsUnique(start, end, uris)).thenReturn(rawData);

        List<StatDto> result = statService.get(start, end, uris, unique);

        assertEquals(4, result.size(), "Количество результатов должно быть равно 1");
        verify(endpointHitRepository, times(1)).findHitsUnique(start, end, uris);
    }

    @Test
    void testfindByTimestampBetweenWhenNoDataFound() {
        List<String> uris = Arrays.asList("/nonexistent/path");
        boolean unique = true;
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now();

        when(endpointHitRepository.findHitsUnique(start, end, uris)).thenReturn(Collections.emptyList());

        List<StatDto> result = statService.get(start, end, uris, unique);

        assertTrue(result.isEmpty(), "Ожидается пустой список результата");
        verify(endpointHitRepository, times(1)).findHitsUnique(start, end, uris);
    }
}