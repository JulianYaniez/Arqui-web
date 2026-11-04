package com.arquiweb.management.refunding.controller;

import com.arquiweb.management.refunding.dto.requests.CreateRefundRequest;
import com.arquiweb.management.refunding.service.RefundingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
            @ApiResponse(responseCode = "403", description = "The reservation does not belong to the requesting customer."),
            @ApiResponse(responseCode = "404", description = "The reservation or associated event does not exist in the system."),
            @ApiResponse(responseCode = "409", description = "The reservation is not in PAID status."),
            @ApiResponse(responseCode = "422", description = "Less than 72 hours until the event, no refunds allowed."),
            @ApiResponse(responseCode = "503", description = "The Ticketing or Catalog microservices are down or unresponsive.")

    })
    @PostMapping
    public ResponseEntity<Void> requestRefund(
            @RequestBody CreateRefundRequest request
    ) {
        // Later replace for JWT
        UUID clientId = UUID.randomUUID();

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
            @ApiResponse(responseCode = "404", description = "The reservation does not exist in the system."),
            @ApiResponse(responseCode = "409", description = "The request is already resolved."),
            @ApiResponse(responseCode = "503", description = "The Ticketing microservice is down or unresponsive.")

    })
    @PostMapping("/{requestId}/accept")
    public ResponseEntity<Void> acceptRefund(
            @PathVariable("requestId") UUID requestId
    ) {
        // Later replace for JWT
        UUID admin = UUID.randomUUID();

        refundingService.acceptRefund(requestId, admin);

        return  ResponseEntity.noContent().build();
    }



    @Operation(summary = "Reject refund request.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Refund request rejected successfully."
            ),
            @ApiResponse(responseCode = "404", description = "The reservation does not exist in the system."),
            @ApiResponse(responseCode = "409", description = "The request is already resolved."),
            @ApiResponse(responseCode = "503", description = "The Ticketing microservice is down or unresponsive.")

    })
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Void> rejectRefund(
            @PathVariable("requestId") UUID requestId
    ) {
        // Later replace for JWT
        UUID admin = UUID.randomUUID();

        refundingService.rejectRefund(requestId, admin);

        return  ResponseEntity.noContent().build();
    }
}
