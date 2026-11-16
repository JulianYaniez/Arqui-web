package com.arquiweb.catalog.venues.controller;

import com.arquiweb.catalog.infrastructure.util.AuthGuard;
import com.arquiweb.catalog.venues.dto.response.VenueResponse;
import com.arquiweb.catalog.venues.dto.request.CreateVenueRequest;
import com.arquiweb.catalog.venues.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/venue")
@RequiredArgsConstructor
@Tag(name = "Venue Management", description = "Operations pertaining venues.")
class VenueController {

    private final VenueService venueService;

    @Operation(summary = "Save new venue.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Saved successfully.",
                    headers = {
                            @Header(
                                    name = "Location",
                                    description = "Saved venue URI.",
                                    schema = @Schema(type = "string")
                            )
                    }
            ),
            @ApiResponse(responseCode = "400", description = "Input is faulty."),
            @ApiResponse(responseCode = "403", description = "Invalid permissions.")
    })
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> createVenue(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String headerUserRole,
            @RequestBody CreateVenueRequest request
    ) {
        AuthGuard.requireRole(headerUserRole, "ORGANIZER", "ADMIN");
        String id = venueService.createVenue(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Find venue by ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Venue fetched successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Given ID has invalid format."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No venue found with ID given."
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<VenueResponse> findVenueById(
            @PathVariable String id
    ) {
        VenueResponse venueQuery = venueService.findVenueById(id);

        return ResponseEntity.ok(venueQuery);
    }
    @Operation(summary = "Find all venues.")
    @GetMapping
    public ResponseEntity<List<VenueResponse>> findAllVenues() {
        List<VenueResponse> venues = venueService.findAllVenues();

        return ResponseEntity.ok(venues);
    }

}
