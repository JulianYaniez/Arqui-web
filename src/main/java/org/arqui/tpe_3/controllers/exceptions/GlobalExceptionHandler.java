package org.arqui.tpe_3.controllers.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.arqui.tpe_3.exceptions.InvalidInputException;
import org.arqui.tpe_3.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(value = InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInputException(
            HttpServletRequest req,
            InvalidInputException ex
    ) {
        var status = HttpStatus.BAD_REQUEST;

        var response = buildResponse(status, ex.getMessage(), req.getRequestURI());

        return new ResponseEntity<>(response, status);
    }


    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            HttpServletRequest req,
            ResourceNotFoundException ex
    ) {
        var status = HttpStatus.NOT_FOUND;

        var response = buildResponse(status, ex.getMessage(), req.getRequestURI());

        return new ResponseEntity<>(response, status);
    }


    // Default
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest req
    ) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;

        var response = buildResponse(status, ex.getMessage(), req.getRequestURI());

        return new ResponseEntity<>(response, status);
    }


    private ErrorResponse buildResponse(
            HttpStatus status,
            String message,
            String uri
    ) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status,
                message,
                uri
        );

    }
}
