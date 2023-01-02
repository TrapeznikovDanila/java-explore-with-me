package ru.practicum.exploreWithMe.exception;

import java.time.LocalDateTime;

public class ValidationException extends RuntimeException {

    String error;
    ErrorStatus status;
    String reason;
    String message;
    LocalDateTime timeStamp;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String error, ErrorStatus status, String reason,
                               String message, LocalDateTime timeStamp) {
        this.error = error;
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timeStamp = timeStamp;
    }
}
