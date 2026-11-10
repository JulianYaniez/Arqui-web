package com.arquiweb.ticketing.inventory.domain.exceptions;

public class IllegalTicketStateChangeException extends RuntimeException {
    public IllegalTicketStateChangeException(String message) {
        super(message);
    }
}
