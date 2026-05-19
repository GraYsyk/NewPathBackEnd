package com.graysenko.NewPathBackEnd.exceptions;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppError {
    private int status;
    private String message;
    private LocalDateTime timestamp;

    public AppError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
