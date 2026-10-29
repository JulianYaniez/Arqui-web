package com.arquiweb.payment.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors
) {

    public ErrorResponse(
        HttpStatus httpStatus, String message, String path, Map<String, String> validationErrors
    ) {
        this(LocalDateTime.now(), httpStatus.value(), httpStatus.getReasonPhrase(), message, path, validationErrors);
    }

    public ErrorResponse(
        HttpStatus httpStatus, String message, String path
    ) {
        this(httpStatus, message, path, null);
    }

}
