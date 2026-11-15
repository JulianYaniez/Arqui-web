package com.arquiweb.identity.infrastructure.exception;

import com.arquiweb.identity.auth.domain.exceptions.InvalidCredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ErrorResponse response = new ErrorResponse(
                httpStatus,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(httpStatus).body(response);
    }


    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInputException(
            InvalidInputException ex,
            HttpServletRequest request
    ) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ErrorResponse response = new ErrorResponse(
                httpStatus,
                ex.getMessage(),
                request.getRequestURI(),
                ex.getValidationErrors()
        );
        return ResponseEntity.status(httpStatus).body(response);
    }

    @ExceptionHandler(value = {
            InvalidCredentialsException.class
    })
    public ResponseEntity<ErrorResponse> handle401(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return buildBasicErrorResponse(HttpStatus.UNAUTHORIZED, ex, request);
    }


    private ResponseEntity<ErrorResponse> buildBasicErrorResponse(HttpStatus httpStatus, RuntimeException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                httpStatus,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(httpStatus).body(response);
    }

}
