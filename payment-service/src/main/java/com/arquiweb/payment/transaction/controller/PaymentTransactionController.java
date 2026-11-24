package com.arquiweb.payment.transaction.controller;

import com.arquiweb.payment.infrastructure.exception.ForbiddenException;
import com.arquiweb.payment.transaction.dtos.request.PaymentRefundRequest;
import com.arquiweb.payment.transaction.dtos.response.PaymentResponseDTO;
import com.arquiweb.payment.transaction.dtos.request.RequestPaymentDTO;
import com.arquiweb.payment.transaction.domain.PaymentTransaction;
import com.arquiweb.payment.transaction.service.PaymentTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.arquiweb.payment.infrastructure.util.AuthGuard;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Tag(
        name = "Payment",
        description = "Operations pertaining payment transaction."
)

public class PaymentTransactionController {

    private final PaymentTransactionService pts;




    @Operation(
            summary = "Process a payment transaction."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Transaction processed and registered successfully."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Request data was faulty or invalid."
                    ),
                    @ApiResponse(
                            responseCode = "502",
                            description = "The release of the reservation could not be confirmed"
                    )
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> processPayment(@Valid @RequestBody RequestPaymentDTO request,
                                                             @RequestHeader(value = "x-Simulate-Fail", required = false) String simulateFail,
                                                             @Parameter(hidden = true)
                                                             @RequestHeader(value = "X-User-Role", required = false) String headerUserRole,
                                                             @Parameter(hidden = true)
                                                             @RequestHeader(value = "X-User-Id", required = false) String headerUserId
    ) {
        AuthGuard.requireRole(headerUserRole, "CLIENT");
        UUID clientId = AuthGuard.requireUserId(headerUserId);

        pts.validateReservationForPayment(
                request.reservationId(),
                clientId,
                request.amount()
        );
        PaymentTransaction transaction = pts.processPayment(request, simulateFail, headerUserRole);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(transaction.getId())
                .toUri();

        PaymentResponseDTO response = new PaymentResponseDTO(
                transaction.getId(),
                transaction.getReservationId(),
                transaction.getStatus(),
                transaction.getAmount()

        );



        return ResponseEntity.created(uri).body(response);
    }



    @Operation(
            summary = "Get a payment transaction by ID."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Payment found successfully."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No payment found with ID given."
                    )
            }
    )

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable UUID id,
                                                             @Parameter(hidden = true)
                                                             @RequestHeader(value = "X-User-Role", required = false) String headerUserRole,
                                                             @Parameter(hidden = true)
                                                             @RequestHeader(value = "X-User-Id", required = false) String headerUserId
    ) {
        AuthGuard.requireRole(headerUserRole, "ADMIN", "CLIENT");

        PaymentTransaction transaction = pts.getById(id);

        if ("CLIENT".equals(headerUserRole == null ? "" : headerUserRole.trim())){
            UUID userId = AuthGuard.requireUserId(headerUserId);

            if (!pts.isReservationOwnedBy(transaction.getReservationId(), userId)) {
                throw new ForbiddenException(
                        "You do not have permission to access this payment."
                );
            }
        }
        PaymentResponseDTO response = new PaymentResponseDTO(
                transaction.getId(),
                transaction.getReservationId(),
                transaction.getStatus(),
                transaction.getAmount()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Refund given payment.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Refund attempted successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found."
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Payment gateway failed."
            )
    })
    @PostMapping("/{id}/refunds")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> refundTransaction(@PathVariable UUID id,
                                                  @RequestHeader(value = "X-Simulate-Fail", required = false) boolean simulateFail,
                                                  @Parameter(hidden = true)
                                                  @RequestHeader(value = "X-User-Role", required = false) String headerUserRole) {
        AuthGuard.requireRole(headerUserRole, "ADMIN");
        pts.refundTransaction(id, simulateFail);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Process bulk refunds for multiple payments."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Bulk refund processed successfully."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Request data was faulty or invalid."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Transaction or reservation not found."
                    )
            }
    )
    @PostMapping("/bulk-refund")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> bulkRefund(@Valid @RequestBody @NotEmpty List<PaymentRefundRequest> refundRequests,
                                           @Parameter(hidden = true)
                                           @RequestHeader(value = "X-User-Role", required = false) String headerUserRole
    ) {
        AuthGuard.requireRole(headerUserRole, "ADMIN");
        pts.processBulkRefund(refundRequests);
        return ResponseEntity.noContent().build();
    }
}
