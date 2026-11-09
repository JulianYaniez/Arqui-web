package com.arquiweb.payment.infrastructure.exception;


import java.util.Map;

public class ConflictException extends RuntimeException {
    private final Map<String, Object> details;

    public ConflictException(String message, Map<String, Object> details) {
        super(message);
        this.details = details;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}