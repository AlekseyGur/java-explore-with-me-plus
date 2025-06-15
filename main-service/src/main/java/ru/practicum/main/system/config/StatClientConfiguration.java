package ru.practicum.main.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import ru.practicum.client.StatClient;

@Configuration
@ComponentScan(basePackages = {
                "ru.practicum.client",
                "ru.practicum.dto"
})
public class StatClientConfiguration {
        @Value("${stats-server-url}")
        private String serverUrl;

        @Bean
        public StatClient statClient(RestClient restClient) {
                return new StatClient(restClient, serverUrl);
        }

        @Bean
        public RestClient restClient() {
                return RestClient.create();
        }
}