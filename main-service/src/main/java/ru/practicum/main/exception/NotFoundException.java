package ru.practicum.main.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class NotFoundException extends BaseException {
    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public NotFoundException(String massage, String reason, LocalDateTime timestamp) {
        super(massage, reason, timestamp);
    }
}
