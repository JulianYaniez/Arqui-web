package com.arquiweb.catalog.events.controller;

import com.arquiweb.catalog.events.dto.request.CreateEventRequest;
import com.arquiweb.catalog.events.service.EventService;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
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
            )
    })
    @PostMapping
    public ResponseEntity<Void> createEvent(
            @RequestBody CreateEventRequest request
    ) {
        String id = eventService.createEvent(request);

        URI uri = UriComponentsBuilder
                .fromUriString("/event/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(uri).build();
    }
}
