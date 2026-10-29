package com.arquiweb.catalog.infrastructure.exception;

import java.util.Collections;
import java.util.Map;

public class InvalidInputException extends RuntimeException {

    private final Map<String, String> validationErrors;

    public InvalidInputException(String message, Map<String, String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors != null ? Map.copyOf(validationErrors) : Collections.emptyMap();
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
}
