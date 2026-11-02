package com.arquiweb.ticketing.reservation.domain.exceptions;

public class QrGenerationException extends RuntimeException {
    public QrGenerationException(String message) {
        super(message);
    }
}
