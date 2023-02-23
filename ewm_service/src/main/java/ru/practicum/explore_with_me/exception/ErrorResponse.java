package ru.practicum.explore_with_me.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ErrorResponse {
    String error;
    ErrorStatus status;
    String reason;
    String message;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    LocalDateTime timeStamp;

    public ErrorResponse(String error, ErrorStatus status, String reason,
                         String message, LocalDateTime timeStamp) {
        this.error = error;
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timeStamp = timeStamp;
    }
}
