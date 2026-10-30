package com.arquiweb.catalog.events.domain.exceptions;

public class IllegalEventStateException extends RuntimeException {
    public IllegalEventStateException(String message) {
        super(message);
    }
}
