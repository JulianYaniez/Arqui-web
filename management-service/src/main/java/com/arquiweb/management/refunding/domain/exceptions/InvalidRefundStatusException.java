package com.arquiweb.management.refunding.domain.exceptions;

public class InvalidRefundStatusException extends RuntimeException {
    public InvalidRefundStatusException(String message) {
        super(message);
    }
}
