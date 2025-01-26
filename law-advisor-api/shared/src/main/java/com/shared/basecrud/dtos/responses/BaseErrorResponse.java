package com.shared.basecrud.dtos.responses;

import java.time.LocalDateTime;

public class BaseErrorResponse {
    private LocalDateTime timestamp;
    private String error;
    private String message;
    private String serviceName;

    public BaseErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public BaseErrorResponse(String error, String message, String serviceName) {
        this();
        this.error = error;
        this.message = message;
        this.serviceName = serviceName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getServiceName() {
        return serviceName;
    }

}
