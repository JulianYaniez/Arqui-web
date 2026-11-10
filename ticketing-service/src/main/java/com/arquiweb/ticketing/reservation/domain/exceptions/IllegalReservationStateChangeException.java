package com.arquiweb.ticketing.reservation.domain.exceptions;

public class IllegalReservationStateChangeException extends RuntimeException {
    public IllegalReservationStateChangeException(String message) {
        super(message);
    }
}
