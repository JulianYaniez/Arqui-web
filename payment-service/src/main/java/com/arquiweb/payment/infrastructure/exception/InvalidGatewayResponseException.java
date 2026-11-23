package com.arquiweb.payment.infrastructure.exception;

public class InvalidGatewayResponseException extends RuntimeException {
    public InvalidGatewayResponseException(String message) {
        super(message);
    }
}
