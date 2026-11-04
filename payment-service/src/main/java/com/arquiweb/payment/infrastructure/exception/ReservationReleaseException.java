package com.arquiweb.payment.infrastructure.exception;

public class ReservationReleaseException extends RuntimeException {
    public ReservationReleaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReservationReleaseException(String message) {
        super(message);
    }
}
