package com.arquiweb.ticketing.reservation.controller;

import com.arquiweb.ticketing.reservation.dto.requests.ReserveNumberedRequest;
import com.arquiweb.ticketing.reservation.dto.requests.ReserveUnnumberedRequest;
import com.arquiweb.ticketing.reservation.dto.responses.ReservationResponse;
import com.arquiweb.ticketing.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
class ReservationController {

    private final ReservationService reservationService;


    @Operation(summary = "Reserve tickets for an unnumbered sector.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation successfully registered."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "More tickets than allowed were requested."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No unnumbered sector exists with given IDs."
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Requested ticket is already reserved or sector has run out of capacity."
            )
    })
    @PostMapping("/unnumbered")
    public ResponseEntity<ReservationResponse> reserveUnnumbered(
            @RequestBody ReserveUnnumberedRequest request
    ) {
        // Placeholder
        String userId = UUID.randomUUID().toString();
        ReservationResponse response = reservationService.reserveUnnumbered(request, userId);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Reserve tickets for an numbered sector.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation successfully registered."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "More tickets than allowed were requested."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Some of the given numbered seats do not exist."
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Requested ticket is already reserved."
            )
    })
    @PostMapping("/numbered")
    public ResponseEntity<ReservationResponse> reserveNumbered(
            @RequestBody ReserveNumberedRequest request
    ) {
        // Placeholder
        String userId = UUID.randomUUID().toString();
        ReservationResponse response = reservationService.reserveNumbered(request, userId);

        return  ResponseEntity.ok(response);
    }

    @Operation(summary = "Cancel reservation pending confirmation.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation cancelled."
            )
    })
    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> paymentFailure(
            @PathVariable("reservationId") UUID id
    ) {
        reservationService.handlePaymentFailure(id);

        return ResponseEntity.noContent().build();
    }
}
