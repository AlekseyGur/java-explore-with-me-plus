package ru.practicum.server.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}