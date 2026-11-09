package com.arquiweb.payment.infrastructure.exception;

public class InvalidCardPrefixException extends RuntimeException {
    public InvalidCardPrefixException(String message) {
        super(message);
    }
}