package com.arquiweb.payment.infrastructure.exception;

public class GatewayRetriesExhaustedException extends RuntimeException {
    public GatewayRetriesExhaustedException(String message, Throwable cause) {
        super(message, cause);
    }
}
