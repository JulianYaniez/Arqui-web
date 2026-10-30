package com.arquiweb.catalog.events.controller;

import com.arquiweb.catalog.events.dto.request.CreateEventRequest;
import com.arquiweb.catalog.events.dto.request.CreateEventSectorRequest;
import com.arquiweb.catalog.events.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
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

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(uri).build();
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
            )
    })
    @PostMapping("/{id}/sectors")
    public ResponseEntity<Void> addEventSector(
            @PathVariable String id,
            @RequestBody CreateEventSectorRequest request
    ) {
        String sectorId = eventService.addSector(id, request);

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
            )
    })
    @PostMapping("/{id}/publish")
    public ResponseEntity<Void> publishEvent(
            @PathVariable String id
    ) {
        eventService.publishEvent(id);
        return ResponseEntity.noContent().build();
    }
}
