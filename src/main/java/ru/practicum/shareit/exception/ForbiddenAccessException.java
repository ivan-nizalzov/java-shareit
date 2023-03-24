package ru.practicum.shareit.exception;

public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException(final String message) {
        super(message);
    }
}
