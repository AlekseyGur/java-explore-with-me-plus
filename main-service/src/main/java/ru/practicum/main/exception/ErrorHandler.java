package ru.practicum.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.server.exception.ErrorResponse;

import java.time.LocalDateTime;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({IncorrectlyDateStateRequestException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleForbiddenException(
            final IncorrectlyDateStateRequestException e
    ) {
        log.debug("Возникла ошибка {},", e.getMessage());
        return Map.of(
                "message", e.getMessage(),
                "reason", e.getReason(),
                "status", "FORBIDDEN",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(
            final NotFoundException e
    ) {
        log.debug("Возникла ошибка {},", e.getMessage());
        return Map.of(
                "message", e.getMessage(),
                "reason", e.getReason(),
                "status", "NOT_FOUND",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler({RequestValidationException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRequestValidationException(
            final RequestValidationException e
    ) {
        log.debug("Возникла ошибка {},", e.getMessage());
        return Map.of(
                "message", e.getMessage(),
                "reason", e.getReason(),
                "status", "BAD_REQUEST",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler({PartialRequestException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handlePartialRequestException(
            final PartialRequestException e
    ) {
        log.debug("Возникла ошибка {},", e.getMessage());
        return Map.of(
                "message", e.getMessage(),
                "reason", e.getReason(),
                "status", "CONFLICT",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler({NameAlreadyExistException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleNameAlreadyExistException(
            final NameAlreadyExistException e
    ) {
        log.debug("Возникла ошибка {},", e.getMessage());
        return Map.of(
                "message", e.getMessage(),
                "reason", e.getReason(),
                "status", "CONFLICT",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleSystemExceptions(final IllegalArgumentException e) {
        log.debug("Возникла ошибка {},", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleSystemExceptions(final NullPointerException e) {
        log.debug("Возникла ошибка {},", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({ResponseStatusException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final RuntimeException e) {
        log.debug("Возникла ошибка {},", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}
