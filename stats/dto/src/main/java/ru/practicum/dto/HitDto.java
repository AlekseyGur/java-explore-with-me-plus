package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

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