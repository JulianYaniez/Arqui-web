package com.arquiweb.identity.auth.dto.responses;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UUID userId,
        String email,
        String role
) {

    public static AuthResponse bearer(String token, long expiresIn, UUID userId, String email, String role) {
        return  new AuthResponse(token, "Bearer", expiresIn, userId, email, role);
    }

}
