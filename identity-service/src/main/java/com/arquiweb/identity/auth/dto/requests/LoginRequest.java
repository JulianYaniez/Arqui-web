package com.arquiweb.identity.auth.dto.requests;

import com.arquiweb.identity.infrastructure.exception.InvalidInputException;

import java.util.HashMap;
import java.util.Map;

public record LoginRequest(
        String email,
        String password
) {

    public LoginRequest {
        Map<String, String> errors = new HashMap<>();

        if (email == null || email.isBlank()) errors.put("email", "Email is required and cannot be blank");
        if (password == null || password.isBlank()) errors.put("password", "Password is required and cannot be blank");

        if (!errors.isEmpty()) throw new InvalidInputException("Login has invalid field/s.", errors);
    }

}
