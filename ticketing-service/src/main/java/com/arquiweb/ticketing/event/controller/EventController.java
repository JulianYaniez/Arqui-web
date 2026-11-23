package com.arquiweb.ticketing.event.controller;

import com.arquiweb.ticketing.event.dto.requests.InitEventRequest;
import com.arquiweb.ticketing.event.service.EventService;
import com.arquiweb.ticketing.infrastructure.utils.AuthGuard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
@Tag(name = "Event Management", description = "Operations pertaining event status.")
public class EventController {


    private final EventService eventService;

    @Operation(summary = "Initialize sector capacities. Used by Catalog Module")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Sectors initialized successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid fields in incoming request body."
            )
    })
    @PostMapping("/init")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> initSectorCapacity(
            @RequestBody InitEventRequest request,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role") String userRole
    ) {
        AuthGuard.requireRole(userRole, "ADMIN");
        eventService.initEvent(request);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cancel Event & Reservations by Event ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Cancelled successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found"
            )
    })
    @PostMapping("/{eventId}/cancel")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> cancelEvent(
            @PathVariable("eventId") String eventId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role") String userRole
    ) {
        AuthGuard.requireRole(userRole, "ADMIN", "ORGANIZER");
        eventService.cancelEvent(eventId);

        return  ResponseEntity.noContent().build();
    }

}
