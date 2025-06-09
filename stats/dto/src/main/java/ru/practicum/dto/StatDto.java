package ru.practicum.dto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatDto {
    private String app;
    private String uri;
    private Long hits;
}