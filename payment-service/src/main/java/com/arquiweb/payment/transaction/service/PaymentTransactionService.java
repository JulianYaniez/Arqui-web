package com.arquiweb.payment.transaction.service;

import com.arquiweb.payment.infrastructure.exception.*;
import com.arquiweb.payment.infrastructure.gateway.dto.response.GatewayResponse;

import com.arquiweb.payment.transaction.domain.Exception.InvalidCardPrefixException;
import com.arquiweb.payment.transaction.dtos.request.PaymentRefundRequest;
import com.arquiweb.payment.transaction.dtos.request.RequestPaymentDTO;
import com.arquiweb.payment.transaction.domain.PaymentTransaction;
import com.arquiweb.payment.transaction.domain.enums.TransactionStatus;
import com.arquiweb.payment.infrastructure.client.TicketingClient;
import com.arquiweb.payment.infrastructure.gateway.PaymentGateway;
import com.arquiweb.payment.transaction.repository.interfaces.PaymentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static com.arquiweb.payment.transaction.domain.enums.PaymentMethod.CREDIT_CARD;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTransactionService {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final TicketingClient ticketingClient;

    private final List<String> validPrefixes = List.of("4111", "4242", "5555", "3782");

    public PaymentTransaction processPayment(RequestPaymentDTO request) {
        if (paymentRepository.isPaymentProcessedAndStatus(request.reservationId(), TransactionStatus.APPROVED)){
            throw new InvalidInputException("Payment already processed",
                    Map.of("reservationId", "Reservation " +
                            request.reservationId() + " has already been paid." ));

        }


        if(request.paymentMethod() == CREDIT_CARD)
            if(!validCardToken(request.cardToken())){ throw new InvalidCardPrefixException("Invalid credit card provider or prefix");}

        PaymentTransaction pt = request.toDomain();
        GatewayResponse gatewayResponse;
        try {
            gatewayResponse = paymentGateway.processPayment(request.cardToken(), request.amount(), request.paymentMethod());
            if (gatewayResponse == null) {
                throw new InvalidInputException("Invalid gateway response",
                        Map.of("gateway", "The payment gateway did not return a valid response."));
            }
            if (gatewayResponse.ts() == null){
                throw new InvalidInputException("Invalid gateway response",
                        Map.of("gateway", "The payment gateway did not return a transaction status."));
            }
        }catch (TimeoutException e) {
            pt = fillAndSave(TransactionStatus.REJECTED, null,LocalDateTime.now(), pt);
            releaseReservation(request.reservationId());
            return pt;
        }
        pt = fillAndSave(gatewayResponse.ts(), gatewayResponse.externalTransactionId(),LocalDateTime.now(), pt);
        if (gatewayResponse.ts() == TransactionStatus.REJECTED) {
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


    public PaymentTransaction getById(UUID id){
        return paymentRepository.getPaymentTransactionById(id).orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found with id: " + id));
    }

    public PaymentTransaction fillAndSave(TransactionStatus status, String externalTransactionId, LocalDateTime processedAt, PaymentTransaction pt){
        pt.setStatus(status);
        pt.setExternalGatewayTransactionId(externalTransactionId);
        pt.setProcessedAt(processedAt);
        return paymentRepository.save(pt);
    }

    @Transactional
    public void refundTransaction(UUID paymentId, Boolean simulateFail) {
        try {


        PaymentTransaction transaction = paymentRepository.getPaymentTransactionByIdWithLock(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + paymentId));
        boolean gatewaySuccess = paymentGateway.refund(transaction.getExternalGatewayTransactionId(), transaction.getAmount(), simulateFail);
        if (gatewaySuccess) {
            transaction.setStatus(TransactionStatus.REFUNDED);
            paymentRepository.save(transaction);
            this.releaseReservation(transaction.getReservationId());
        }else{
            throw new PaymentGatewayException("the payment gateway refund failed");
        }}catch (PessimisticLockingFailureException e){
            throw new ConflictException("The resource is currently being modified by another request.", Map.of("paymentId", paymentId.toString()));
        }
    }

    public boolean validCardToken(String cardToken){
        String token = cardToken.trim();
        if(token.length() != 16) return false;
        String Prefix = token.substring(0, 4);

        return validPrefixes.contains(Prefix);
    }

    @Transactional
    public void processBulkRefund(List<PaymentRefundRequest> refundRequests) {
        for (PaymentRefundRequest request: refundRequests){
            try {


            PaymentTransaction transaction = paymentRepository.getPaymentTransactionByIdWithLock(request.paymentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction not found for reservation: " + request.reservationId()));
            if(transaction.isRefunded()){
                continue;
            }

            boolean gatewaySuccess = paymentGateway.refund(
                    transaction.getExternalGatewayTransactionId(),
                    request.totalAmound(),
                    false
            );
            if (!gatewaySuccess) {
                throw new PaymentGatewayException("Payment gateway refund failed for reservation: " + request.reservationId());
            }
            transaction.setStatus(TransactionStatus.REFUNDED);
            paymentRepository.save(transaction);
            this.releaseReservation(transaction.getReservationId());
        }
        catch(Exception e){
            throw new ConflictException("The resource is currently being modified by another request.", Map.of("paymentId", request.paymentId().toString()));
        }

    }}
}
