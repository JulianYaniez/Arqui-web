package com.arquiweb.payment.transaction.domain.Exception;

public class InvalidCardPrefixException extends RuntimeException {
    public InvalidCardPrefixException(String message) {
        super(message);
    }
}