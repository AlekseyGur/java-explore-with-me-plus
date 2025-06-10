package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import javax.validation.Valid;
import java.util.List;

@Service
public class StatClient {
    @Value("stats-server:9090")
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
        } catch (RestClientException e) {
            handleRestClientException(e, "Ошибка при отправке hit");
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
        } catch (RestClientException e) {
            handleRestClientException(e, "Ошибка при получении статистики");
        }
        return List.of();
    }
}