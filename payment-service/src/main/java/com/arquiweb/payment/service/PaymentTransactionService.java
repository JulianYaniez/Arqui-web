package com.arquiweb.payment.service;

import com.arquiweb.payment.dtos.GatewayResponse;
import com.arquiweb.payment.dtos.SavePaymentDTO;
import com.arquiweb.payment.entity.PaymentTransaction;
import com.arquiweb.payment.enums.TransactionStatus;
import com.arquiweb.payment.infrastructure.exception.InvalidInputException;
import com.arquiweb.payment.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.payment.infrastructure.gateway.PaymentGateway;
import com.arquiweb.payment.repositori.interfaces.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentTransactionService {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;

    public PaymentTransaction processPayment(SavePaymentDTO request) {

        if (paymentRepository.isPaymentProcessedAndStatus(request.reservationId(), TransactionStatus.APPROVED)){
            throw new InvalidInputException("Payment already processed",
                    Map.of("reservationId", "Reservation " +
                            request.reservationId() + " has already been paid." ));

        }
        PaymentTransaction pt = request.toDomain();
        pt.setProcessedAt(LocalDateTime.now());
        GatewayResponse gr = paymentGateway.processPayment(request.cardToken(), request.amount());

        pt.setStatus(gr.ts());
        pt.setExternalGateweyTransactionId(gr.externalTransactionId());

        return paymentRepository.save(pt);
    }

    public boolean isPaymentProcessedAndStatus(UUID reservationId, TransactionStatus status){
        return paymentRepository.isPaymentProcessedAndStatus(reservationId, status);

    }

    public PaymentTransaction getById(UUID id){
        return paymentRepository.getPaymentTransactionById(id).orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found with id: " + id));
    }
}
