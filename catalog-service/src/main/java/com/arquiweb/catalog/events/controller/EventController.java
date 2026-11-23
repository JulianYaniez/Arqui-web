

package com.arquiweb.catalog.events.controller;

import com.arquiweb.catalog.events.dto.request.CreateEventRequest;
import com.arquiweb.catalog.events.dto.request.CreateEventSectorRequest;
import com.arquiweb.catalog.events.dto.request.EventCriteriaQuery;
import com.arquiweb.catalog.events.dto.response.EventResponse;
import com.arquiweb.catalog.events.dto.response.EventStateResponse;
import com.arquiweb.catalog.events.service.EventService;
import com.arquiweb.catalog.infrastructure.util.AuthGuard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
@Tag(name = "Event Management", description = "Operations pertaining events.")
class EventController {

    private final EventService eventService;

    @Operation(summary = "Save a new event as a draft.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Saved successfully.",
                    headers = {
                            @Header(
                                    name = "Location",
                                    description = "Saved event URI.",
                                    schema = @Schema(type = "string")
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Some of the given IDs are invalid."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Data with some of the given IDs could not be found."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid identifier."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Invalid permissions."
            )
    })
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> createEvent(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Id", required = false ) String headerUserId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false ) String headerUserRole,
            @RequestBody CreateEventRequest request
    ) {
        UUID organizerId = AuthGuard.requireUserId(headerUserId);
        AuthGuard.requireRole(headerUserRole, "ORGANIZER", "ADMIN");

        String id = eventService.createEvent(organizerId, request);

        URI uri = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();

        return ResponseEntity.created(uri).build();
   }   

    @Operation(summary = "Get event state by ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Fetched successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No event found with given ID."
            )
    })
    @GetMapping("/{eventId}/state")
    public ResponseEntity<EventStateResponse> getEventStateById(
            @PathVariable("eventId") String eventId
    ) {
        EventStateResponse response = eventService.getById(eventId);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Add sector to drafted event.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Sector added successfully.",
                    headers = @Header(
                            name = "Location",
                            description = "Saved event sector URI"
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Given event ID is invalid."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Given event could not be found or sector does not belong to event's venue."
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Cannot add sectors to an event unless it is still a DRAFT."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid identifier."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Invalid permissions."
            )
    })
    @PostMapping("/{id}/sectors")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> addEventSector(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Id", required = false ) String headerUserId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false ) String headerUserRole,
            @PathVariable String id,
            @RequestBody CreateEventSectorRequest request
    ) {
        UUID organizerId = AuthGuard.requireUserId(headerUserId);
        AuthGuard.requireRole(headerUserRole, "ORGANIZER", "ADMIN");

        String sectorId = eventService.addSector(organizerId, id, request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}/sectors/{sectorId}")
                .buildAndExpand(id, sectorId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }


    @Operation(summary = "Publish a drafted event.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Event published successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Given event could not be found."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ticketing service could not respond properly."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid identifier."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Invalid permissions."
            )
    })
    @PostMapping("/{id}/publish")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> publishEvent(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Id", required = false ) String headerUserId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false ) String headerUserRole,
            @PathVariable String id
    ) {
        UUID organizerId = AuthGuard.requireUserId(headerUserId);
        AuthGuard.requireRole(headerUserRole, "ORGANIZER", "ADMIN");

        eventService.publishEvent(organizerId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all events filtered by multiple criteria.")
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<EventResponse>> findAllEvents(
        @Parameter(hidden = true)
        @RequestHeader(value = "X-User-Role", required = false) String headerUserRole,

        @ModelAttribute("criteria") EventCriteriaQuery criteria,

        @PageableDefault(
                size = 20,
                sort = "dateTime",
                direction = Sort.Direction.ASC
        ) Pageable pageable
    ) {
        System.out.println("USER ROLE: " + headerUserRole);
        Page<EventResponse> page =
                eventService.getAll(criteria, pageable, headerUserRole);

        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Get recommended events.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Fetched successfully."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid identity."
            )
    })
    @GetMapping("/recommend")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<EventResponse>> findRecommendedEvents(
            @PageableDefault(size = 20, sort = "dateTime", direction = Sort.Direction.ASC) Pageable pageable,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId
    ) {
        UUID userId = AuthGuard.requireUserId(headerUserId);

        Page<EventResponse> page = eventService.getRecommended(userId, pageable);

        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Cancel event.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Cancelled successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found."
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid event state."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid identifier."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Invalid permissions."
            )
    })
    @PostMapping("/{eventId}/cancel")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> cancelEvent(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Id", required = false ) String headerUserId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false ) String headerUserRole,
            @PathVariable("eventId") String eventId
    ) {
        UUID organizerId = AuthGuard.requireUserId(headerUserId);
        AuthGuard.requireRole(headerUserRole, "ORGANIZER", "ADMIN");

        eventService.cancelEvent(organizerId, eventId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get event detail by ID.")
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Event found."
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Event not found with given ID."
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(
        @PathVariable String id
    ) {
        EventResponse response = eventService.getEventById(id);

        return ResponseEntity.ok(response);
    }

}
