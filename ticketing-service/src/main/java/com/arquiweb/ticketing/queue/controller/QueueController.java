package com.arquiweb.ticketing.queue.controller;

import com.arquiweb.ticketing.infrastructure.utils.AuthGuard;
import com.arquiweb.ticketing.queue.dto.response.QueueStatusResponse;
import com.arquiweb.ticketing.queue.service.VirtualQueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
@Tag(name = "Queue Management", description = "Join and query queues.")
public class QueueController {


    private final VirtualQueueService virtualQueueService;

    @PostMapping("/join/{eventId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Join event queue.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Joined successfully."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid identity."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found."
            )
    })
    public ResponseEntity<QueueStatusResponse> joinQueue(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId,
            @PathVariable String eventId
    ) {
        UUID userId = AuthGuard.requireUserId(headerUserId);
        QueueStatusResponse response = virtualQueueService.joinQueue(eventId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{eventId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get personal queue status.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Status fetched successfully."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid identity."
            )
    })
    public ResponseEntity<QueueStatusResponse> getQueueStatus(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId,
            @PathVariable String eventId
    ) {
        UUID userId = AuthGuard.requireUserId(headerUserId);
        QueueStatusResponse response = virtualQueueService.getStatus(eventId, userId);
        return ResponseEntity.ok(response);
    }
}
