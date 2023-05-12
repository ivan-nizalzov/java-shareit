package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleMissingIdException(NotFoundException e) {
        log.warn("404 {}", e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessage handleFailedOwnerException(ForbiddenAccessException e) {
        log.warn("403 {}", e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleRequestFailedException(BadRequestException e) {
        log.warn("400 {}", e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleRequestUnsupportedStatus(UnsupportedStatus e) {
        log.warn("400 {}", e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUnknownDataException(TimeDataException e) {
        log.warn("400 {}", e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

}
