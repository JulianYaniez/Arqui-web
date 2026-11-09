package com.arquiweb.payment.transaction.service;

import com.arquiweb.payment.infrastructure.gateway.dto.response.GatewayResponse;
import com.arquiweb.payment.transaction.dtos.request.SavePaymentDTO;
import com.arquiweb.payment.transaction.domain.PaymentTransaction;
import com.arquiweb.payment.transaction.domain.enums.TransactionStatus;
import com.arquiweb.payment.infrastructure.client.TicketingClient;
import com.arquiweb.payment.infrastructure.exception.InvalidInputException;
import com.arquiweb.payment.infrastructure.exception.PaymentGatewayException;
import com.arquiweb.payment.infrastructure.exception.ReservationReleaseException;
import com.arquiweb.payment.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.payment.infrastructure.gateway.PaymentGateway;
import com.arquiweb.payment.transaction.repositori.interfaces.PaymentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTransactionService {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final TicketingClient ticketingClient;

    public PaymentTransaction processPayment(SavePaymentDTO request) {
        if (paymentRepository.isPaymentProcessedAndStatus(request.reservationId(), TransactionStatus.APPROVED)){
            throw new InvalidInputException("Payment already processed",
                    Map.of("reservationId", "Reservation " +
                            request.reservationId() + " has already been paid." ));

        }
        PaymentTransaction pt = request.toDomain();
        GatewayResponse gr;
        try {
            gr = paymentGateway.processPayment(request.cardToken(), request.amount());
            if (gr == null) {
                throw new InvalidInputException("Invalid gateway response",
                        Map.of("gateway", "The payment gateway did not return a valid response."));
            }
            if (gr.ts() == null){
                throw new InvalidInputException("Invalid gateway response",
                        Map.of("gateway", "The payment gateway did not return a transaction status."));
            }
        }catch (TimeoutException e) {
            pt = fillAndSave(TransactionStatus.REJECTED, null,LocalDateTime.now(), pt);
            releaseReservation(request.reservationId());
            return pt;
        }
        pt = fillAndSave(gr.ts(), gr.externalTransactionId(),LocalDateTime.now(), pt);
        if (gr.ts() == TransactionStatus.REJECTED) {
            releaseReservation(request.reservationId());
        }
        log.info("Payment transaction processed successfully, id={}, reservationId={}, staus={}", pt.getId(), pt.getReservationId(), pt.getStatus());
        return pt;

    }

    private void releaseReservation(UUID reservationId) {
        try {
            ticketingClient.releaseReservations(reservationId);
        } catch (FeignException e){
            log.error("Error communicating with the ticketing service to release the reservation id={}: {}", reservationId, e.getMessage());
            throw new ReservationReleaseException(
                    "The release of the reservation could not be confirmed: " + reservationId,
                    e
            );
        }}


    public boolean isPaymentProcessedAndStatus(UUID reservationId, TransactionStatus status){
        return paymentRepository.isPaymentProcessedAndStatus(reservationId, status);

    }

    public PaymentTransaction getById(UUID id){
        return paymentRepository.getPaymentTransactionById(id).orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found with id: " + id));
    }

    public PaymentTransaction fillAndSave(TransactionStatus status, String externalTransactionId, LocalDateTime processedAt, PaymentTransaction pt){
        pt.setStatus(status);
        pt.setExternalGateweyTransactionId(externalTransactionId);
        pt.setProcessedAt(processedAt);
        return paymentRepository.save(pt);
    }

    public void refundTransaction(UUID paymentId, Boolean simulateFail) {
        PaymentTransaction transaction = paymentRepository.getPaymentTransactionById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + paymentId));
        boolean gatewaySuccess = paymentGateway.refund(transaction.getExternalGateweyTransactionId(), transaction.getAmount(), simulateFail);
        if (gatewaySuccess) {
            transaction.setStatus(TransactionStatus.REFUNDED);
            paymentRepository.save(transaction);
            this.releaseReservation(transaction.getReservationId());
        }else{
            throw new PaymentGatewayException("the payment gateway refund failed");
        }
    }


}
