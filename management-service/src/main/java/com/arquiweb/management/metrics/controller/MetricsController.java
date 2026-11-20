package com.arquiweb.management.metrics.controller;

import com.arquiweb.management.infrastructure.utils.AuthGuard;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/metric")
@RequiredArgsConstructor
public class MetricsController {

    @GetMapping("/event/{eventId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> getEventMetrics(
            @PathVariable("eventId") String eventId,
            @RequestHeader(value = "X-User-Id", required = false) String headerOrganizerId,
            @RequestHeader(value = "X-User-Role", required = false) String headerUserRole
    ) {
        AuthGuard.requireRole(headerUserRole, "ORGANIZER", "ADMIN");
        UUID organizerId = AuthGuard.requireUserId(headerOrganizerId);

        return null;
    }

}
