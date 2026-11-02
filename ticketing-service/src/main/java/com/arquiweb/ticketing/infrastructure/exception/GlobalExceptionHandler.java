package com.arquiweb.ticketing.infrastructure.exception;

import com.arquiweb.ticketing.inventory.domain.exceptions.IllegalTicketStateChangeException;
import com.arquiweb.ticketing.reservation.domain.exceptions.IllegalReservationStateChangeException;
import com.arquiweb.ticketing.reservation.domain.exceptions.OccupiedSpotException;
import com.arquiweb.ticketing.reservation.domain.exceptions.QrGenerationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class) // 404
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildBasicErrorResponse(HttpStatus.NOT_FOUND, ex, request);
    }


    @ExceptionHandler(InvalidInputException.class) // 400
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

    @ExceptionHandler({ // 409
            OptimisticLockingFailureException.class,
            PessimisticLockingFailureException.class,
            OccupiedSpotException.class,
            IllegalTicketStateChangeException.class,
            IllegalReservationStateChangeException.class
    })
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailureException(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return buildBasicErrorResponse(HttpStatus.CONFLICT, ex, request);
    }


    @ExceptionHandler({ // 500
            QrGenerationException.class
    })
    public ResponseEntity<ErrorResponse> handleHttp500(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return buildBasicErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
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
