package ru.practicum.main.system.exception;

public class BadConditionsException extends RuntimeException {
    public BadConditionsException(String message) {
        super(message);
    }
}