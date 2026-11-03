package com.arquiweb.management.refunding.domain.exceptions;

public class UnauthorizedRefundRequestException extends RuntimeException {
    public UnauthorizedRefundRequestException(String message) {
        super(message);
    }
}
