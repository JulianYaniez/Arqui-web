package com.arquiweb.ticketing.inventory.domain.exceptions;

public class IllegalStateChangeException extends RuntimeException {
    public IllegalStateChangeException(String message) {
        super(message);
    }
}
