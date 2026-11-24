package com.arquiweb.management.refunding.controller;

import com.arquiweb.management.infrastructure.utils.AuthGuard;
import com.arquiweb.management.refunding.dto.requests.CreateRefundRequest;
import com.arquiweb.management.refunding.service.RefundingService;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/refunds")
@Tag(name = "Refunding Management", description = "Operations pertaining refund management.")
public class RefundingController {

    private final RefundingService refundingService;


    @Operation(summary = "Create new refund request.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Refund request created successfully.",
                    headers = {
                            @Header(
                                    name = "Location",
                                    description = "Saved request URI.",
                                    schema = @Schema(type = "string")
                            )
                    }
            ),
            @ApiResponse(responseCode = "401", description = "Invalid identity."),
            @ApiResponse(responseCode = "403", description = "Invalid permissions."),
            @ApiResponse(responseCode = "404", description = "The reservation or associated event does not exist in the system."),
            @ApiResponse(responseCode = "409", description = "The reservation is not in PAID status."),
            @ApiResponse(responseCode = "422", description = "Less than 72 hours until the event, no refunds allowed."),
            @ApiResponse(responseCode = "503", description = "The Ticketing or Catalog microservices are down or unresponsive.")

    })
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> requestRefund(
            @RequestBody CreateRefundRequest request,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String headerUserRole
    ) {
        UUID clientId = AuthGuard.requireUserId(headerUserId);
        AuthGuard.requireRole(headerUserRole, "CLIENT", "ORGANIZER", "ADMIN");

        UUID id = refundingService.createRefundRequest(request, clientId);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return  ResponseEntity.created(uri).build();
    }


    @Operation(summary = "Accept refund request.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Refund request accepted successfully."
            ),
            @ApiResponse(responseCode = "401", description = "Invalid identity."),
            @ApiResponse(responseCode = "403", description = "Invalid permissions."),
            @ApiResponse(responseCode = "404", description = "The reservation does not exist in the system."),
            @ApiResponse(responseCode = "409", description = "The request is already resolved."),
            @ApiResponse(responseCode = "503", description = "The Ticketing microservice is down or unresponsive.")

    })
    @PostMapping("/{requestId}/accept")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> acceptRefund(
            @PathVariable("requestId") UUID requestId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String headerUserRole
    ) {
        AuthGuard.requireRole(headerUserRole, "ADMIN");
        UUID adminId = AuthGuard.requireUserId(headerUserId);

        refundingService.acceptRefund(requestId, adminId, headerUserRole);

        return  ResponseEntity.noContent().build();
    }



    @Operation(summary = "Reject refund request.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Refund request rejected successfully."
            ),
            @ApiResponse(responseCode = "401", description = "Invalid identity."),
            @ApiResponse(responseCode = "403", description = "Invalid permissions."),
            @ApiResponse(responseCode = "404", description = "The reservation does not exist in the system."),
            @ApiResponse(responseCode = "409", description = "The request is already resolved."),
            @ApiResponse(responseCode = "503", description = "The Ticketing microservice is down or unresponsive.")

    })
    @PostMapping("/{requestId}/reject")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> rejectRefund(
            @PathVariable("requestId") UUID requestId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String headerUserRole
    ) {
        AuthGuard.requireRole(headerUserRole, "ADMIN");
        UUID adminId = AuthGuard.requireUserId(headerUserId);

        refundingService.rejectRefund(requestId, adminId, headerUserRole);

        return  ResponseEntity.noContent().build();
    }


    @Operation(summary = "Cancel event and execute refunds.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Event successfully canceled."
            ),
            @ApiResponse(responseCode = "401", description = "Invalid identity"),
            @ApiResponse(responseCode = "403", description = "Invalid permissions."),
            @ApiResponse(
                    responseCode = "404",
                    description = "No event found with given ID"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Catalog, Ticketing or Payment services are down or could not respond properly."
            )
    })
    @PostMapping("/cancel-event")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> cancelEvent(
            @RequestParam("eventId") String eventId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String headerUserRole,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId
    ) {
        AuthGuard.requireRole(headerUserRole, "ADMIN");
        UUID adminId = AuthGuard.requireUserId(headerUserId);
        refundingService.cancelEvent(eventId, adminId, headerUserRole );

        return ResponseEntity.noContent().build();
    }
}
