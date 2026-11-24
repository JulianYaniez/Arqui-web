package com.arquiweb.management.metrics.controller;

import com.arquiweb.management.infrastructure.utils.AuthGuard;
import com.arquiweb.management.metrics.dto.responses.EventMetricsResponse;
import com.arquiweb.management.metrics.service.MetricsService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/metric")
@RequiredArgsConstructor
@Tag(name = "Metrics Manager", description = "Request event metrics.")
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping("/event/{eventId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<EventMetricsResponse> getEventMetrics(
            @PathVariable("eventId") String eventId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) String headerOrganizerId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String headerUserRole
    ) {
        AuthGuard.requireRole(headerUserRole, "ORGANIZER", "ADMIN");
        UUID organizerId = AuthGuard.requireUserId(headerOrganizerId);

        var response = metricsService.getEventMetrics(eventId, organizerId);

        return ResponseEntity.ok(response);
    }

}
