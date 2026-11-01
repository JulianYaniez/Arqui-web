package com.arquiweb.ticketing.reservation.domain.exceptions;

public class OccupiedSpotException extends RuntimeException {
    public OccupiedSpotException(String message) {
        super(message);
    }
}
