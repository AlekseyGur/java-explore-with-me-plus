package ru.practicum.main.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class PartialRequestException extends BaseException {
    private final HttpStatus status = HttpStatus.CONFLICT;

    public PartialRequestException(String massage, String reason, LocalDateTime timestamp) {
        super(massage, reason, timestamp);
    }
}
