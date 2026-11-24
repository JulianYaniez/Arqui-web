package com.arquiweb.payment.infrastructure.util;

import com.arquiweb.payment.infrastructure.exception.ForbiddenException;
import com.arquiweb.payment.infrastructure.exception.UnauthorizedException;

import java.util.List;
import java.util.UUID;

public final class AuthGuard {

    public static UUID requireUserId(String headerUserId) {
        if (headerUserId == null || headerUserId.isBlank()) {
            throw new UnauthorizedException("Authentication is required to access this resource.");
        }
        try {
            return UUID.fromString(headerUserId.trim());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid authentication.");
        }
    }

    public static void requireRole(String headerUserRole, String... allowedRoles) {
        if (headerUserRole == null || !List.of(allowedRoles).contains(headerUserRole.trim())) {
            String accepted = String.join(", ", allowedRoles);
            throw new ForbiddenException("Invalid permissions. Access denied. Allowed Roles: [" + accepted + "].");
        }
    }

}
