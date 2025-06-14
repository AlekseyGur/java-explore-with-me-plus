package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HitDto {
    @NotBlank(message = "APP не может быть пустым")
    @Size(min = 1, max = 1000)
    private String app;

    @NotBlank(message = "URI не может быть пустым")
    @Size(min = 1, max = 1000)
    @Pattern(regexp = "^/.*", message = "URI должен начинаться с /. Максимальная длина 1000, минимальная 1")
    private String uri;

    @NotBlank(message = "IP-адрес не может быть пустым")
    @Size(min = 7, max = 15)
    @Pattern(regexp = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$", message = "Неверный формат IP-адреса v4")
    private String ip;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "Временная метка не может быть пустой")
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
}