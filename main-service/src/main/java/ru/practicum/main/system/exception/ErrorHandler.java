package ru.practicum.main.system.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError constraintViolationException(final ConstraintViolationException e) {
        String reason = "Недопустимое значение параметра";
        log.error(reason + ". " + e.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST, reason, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError internalServerException(final InternalServerException e) {
        String reason = "Ошибка сервера";
        log.error(reason + ". " + e.getMessage());
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, reason, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError accessDeniedException(final AccessDeniedException e) {
        String reason = "Доступ заблокирован";
        log.error(reason + ". " + e.getMessage());
        return new ApiError(HttpStatus.FORBIDDEN, reason, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conditionsNotMetException(final BadConditionsException e) {
        String reason = "Условия не выполнены";
        log.error(reason + ". " + e.getMessage());
        return new ApiError(HttpStatus.CONFLICT, reason, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError duplicatedDataException(final DuplicatedDataException e) {
        String reason = "Дубликат данных";
        log.error(reason + ". " + e.getMessage());
        return new ApiError(HttpStatus.CONFLICT, reason, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFoundException(final NotFoundException e) {
        String reason = "Ресурс не найден";
        log.error(reason + ". " + e.getMessage());
        return new ApiError(HttpStatus.NOT_FOUND, reason, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError validationException(final ValidationException e) {
        String reason = "Данные не прошли проверку";
        log.error(reason + ". " + e.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST, reason, e.getMessage());
    }
}