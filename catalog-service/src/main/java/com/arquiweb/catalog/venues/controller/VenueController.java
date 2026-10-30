package com.arquiweb.catalog.venues.controller;

import com.arquiweb.catalog.venues.domain.Venue;
import com.arquiweb.catalog.venues.dto.request.CreateVenueRequest;
import com.arquiweb.catalog.venues.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/venue")
@RequiredArgsConstructor
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
            @ApiResponse(responseCode = "400", description = "Input is faulty.")
    })
    @PostMapping
    public ResponseEntity<Void> createVenue(
            @RequestBody CreateVenueRequest request
    ) {
        String id = venueService.createVenue(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }
}
