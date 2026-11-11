package com.arquiweb.catalog.events.controller;

import com.arquiweb.catalog.events.dto.request.CreateEventRequest;
import com.arquiweb.catalog.events.dto.request.CreateEventSectorRequest;
import com.arquiweb.catalog.events.dto.request.EventCriteriaQuery;
import com.arquiweb.catalog.events.dto.response.EventResponse;
import com.arquiweb.catalog.events.dto.response.EventStateResponse;
import com.arquiweb.catalog.events.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


    @Operation(summary = "Get all events filtered by multiple criteria.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Fetched successfully."
            )
    })
    @GetMapping
    public ResponseEntity<Page<EventResponse>> findAllEvents(
            @ModelAttribute("criteria") EventCriteriaQuery criteria,
            @PageableDefault(size = 20, sort = "dateTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<EventResponse> page = eventService.getAll(criteria, pageable);

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
            )
    })
    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<Void> cancelEvent(
            @PathVariable("eventId") String eventId
    ) {
        eventService.cancelEvent(eventId);
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
