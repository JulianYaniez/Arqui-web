package com.arquiweb.payment.controller;

import com.arquiweb.payment.dtos.PaymentResponseDTO;
import com.arquiweb.payment.dtos.SavePaymentDTO;
import com.arquiweb.payment.entity.PaymentTransaction;
import com.arquiweb.payment.service.PaymentTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Tag(
        name = "Payment",
        description = "Endpoints for  payment tranasction."
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
                    )
            }
    )
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> prcessPayment(@Valid @RequestBody SavePaymentDTO request){
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
            summary = "get a PaymentTransaction"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "payment was found successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "no payment found with id given"
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


}
