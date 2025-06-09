package ru.practicum.dto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HitDto {
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}