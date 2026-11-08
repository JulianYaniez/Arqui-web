package com.arquiweb.ticketing.event.controller;

import com.arquiweb.ticketing.event.dto.requests.InitEventRequest;
import com.arquiweb.ticketing.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
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
    public ResponseEntity<Void> initSectorCapacity(
            @RequestBody InitEventRequest request
    ) {
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
    public ResponseEntity<Void> cancelEvent(
            @PathVariable("eventId") String eventId
    ) {
        eventService.cancelEvent(eventId);

        return  ResponseEntity.noContent().build();
    }

}
