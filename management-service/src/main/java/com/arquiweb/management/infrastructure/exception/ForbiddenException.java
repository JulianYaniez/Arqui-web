package com.arquiweb.management.infrastructure.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String s) {
        super(s);
    }
}
