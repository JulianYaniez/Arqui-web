package com.arquiweb.payment.transaction.controller;

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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> processPayment(@Valid @RequestBody RequestPaymentDTO request){
        PaymentTransaction transaction = pts.processPayment(request);

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
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable UUID id) {
        PaymentTransaction transaction = pts.getById(id);
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
    public ResponseEntity<Void> refundTransaction(@PathVariable UUID id,
                                                  @RequestHeader(value = "X-Simulate-Fail", required = false) boolean simulateFail) {
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
    public ResponseEntity<Void> bulkRefund(@Valid @RequestBody List<PaymentRefundRequest> refundRequests) {
        pts.processBulkRefund(refundRequests);
        return ResponseEntity.noContent().build();
    }
}
