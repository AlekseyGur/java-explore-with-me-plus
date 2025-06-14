package ru.practicum.main.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class RequestValidationException extends BaseException {
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public RequestValidationException(String massage, String reason, LocalDateTime timestamp) {
        super(massage, reason, timestamp);
    }
}
