package ru.practicum.server.model;

import java.time.LocalDateTime;

import javax.validation.constraints.*;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "hits")
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "app", nullable = false)
    @Size(min = 1, max = 100)
    private String app;

    @Column(name = "uri", nullable = false)
    @Size(min = 1, max = 100)
    private String uri;

    @Column(name = "ip", nullable = false)
    @Size(min = 7, max = 15)
    private String ip;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public EndpointHit(String app, String uri, String ip, LocalDateTime timestamp) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timestamp = timestamp;
    }
}