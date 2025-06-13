package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Service
public class StatClient {
    @Value("stats-server-url")
    String serverUrl;

    private final RestClient restClient;

    StatClient() {
        restClient = RestClient.create(serverUrl);
    }

    public void hit(@Valid HitDto hitDto) {
        try {
            restClient.post().uri("/hit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(hitDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Ошибка при отправке hit");
        }
    }

    public List<StatDto> getStats(String start,
            String end,
            List<String> uris,
            Boolean unique) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/stats")
                            .queryParam("start", start)
                            .queryParam("end", end)
                            .queryParam("uris", uris)
                            .queryParam("unique", unique)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<StatDto>>() {
                    });
        } catch (Exception e) {
            log.error("Ошибка при получении статистики");
        }
        return List.of();
    }
}