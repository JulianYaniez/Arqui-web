package com.arquiweb.management.infrastructure.exception;

import com.arquiweb.management.refunding.domain.exceptions.UnauthorizedRefundRequestException;
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
        return buildBasicErrorResponse(HttpStatus.NOT_FOUND, ex, request);
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
            UnauthorizedRefundRequestException.class
    })
    private ResponseEntity<ErrorResponse>  handle403(
                RuntimeException ex,
                HttpServletRequest request
    ) {
        return buildBasicErrorResponse(HttpStatus.FORBIDDEN, ex, request);
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
