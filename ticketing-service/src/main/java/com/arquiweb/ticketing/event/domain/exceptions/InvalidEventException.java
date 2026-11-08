package com.arquiweb.ticketing.event.domain.exceptions;

public class InvalidEventException extends RuntimeException {
    public InvalidEventException(String message) {
        super(message);
    }
}
