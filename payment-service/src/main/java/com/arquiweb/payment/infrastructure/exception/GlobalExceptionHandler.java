package com.arquiweb.payment.infrastructure.exception;

import com.arquiweb.payment.transaction.domain.Exception.InvalidCardPrefixException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.PessimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.Map;

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

    @ExceptionHandler(ReservationReleaseException.class)
    public ResponseEntity<Map<String, Object>> handleReservationReleaseException(ReservationReleaseException ex) {
        Map<String, Object> body = Map.of(
                "error", "Error de integración con Ticketing",
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity<ErrorResponse> handlePaymentGatewayException(
            PaymentGatewayException ex,
            HttpServletRequest request
    ) {
        HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_CONTENT;
        ErrorResponse response = new ErrorResponse(
                httpStatus,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(httpStatus).body(response);
    }


    @ExceptionHandler(InvalidCardPrefixException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCardPrefixException(
            InvalidCardPrefixException ex,
            HttpServletRequest request
    ) {
        HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_CONTENT;
        ErrorResponse response = new ErrorResponse(
                httpStatus,
                ex.getMessage(),
                request.getRequestURI(),
                Map.of("cardToken", ex.getMessage())
        );
        return ResponseEntity.status(httpStatus).body(response);
    }

    @ExceptionHandler({
            PessimisticLockingFailureException.class,
            CannotAcquireLockException.class
    })
    public ResponseEntity<ErrorResponse> handleLockExceptions(
            Exception ex,
            HttpServletRequest request
    ) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        ErrorResponse response = new ErrorResponse(
                httpStatus,
                "The resource is currently being modified by another concurrent request. Please try again later.",
                request.getRequestURI()
        );
        return ResponseEntity.status(httpStatus).body(response);
    }

}
