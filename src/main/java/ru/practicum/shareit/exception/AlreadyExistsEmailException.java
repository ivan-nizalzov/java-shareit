package ru.practicum.shareit.exception;

public class AlreadyExistsEmailException extends RuntimeException {
    public AlreadyExistsEmailException(final String message) {
        super(message);
    }
}
