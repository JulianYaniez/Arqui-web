package com.arquiweb.payment.transaction.service;

import com.arquiweb.payment.infrastructure.breaker.CircuitBreaker;
import com.arquiweb.payment.infrastructure.client.dto.ReservationStateResponse;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    private static final String PAYMENT_SERVICE_ROLE = "PAYMENT_SERVICE";

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final TicketingClient ticketingClient;
    private final CircuitBreaker circuitBreaker;

    private final List<String> validPrefixes = List.of("4111", "4242", "5555", "3782");

    public PaymentTransaction processPayment(RequestPaymentDTO request, String simulateFail) {
        if (paymentRepository.isPaymentProcessedAndStatus(request.reservationId(), TransactionStatus.APPROVED)){
            throw new InvalidInputException("Payment already processed",
                    Map.of("reservationId", "Reservation " +
                            request.reservationId() + " has already been paid." ));

        }


        if(request.paymentMethod() == CREDIT_CARD)
            if(!validCardToken(request.cardToken())){ throw new InvalidCardPrefixException("Invalid credit card provider or prefix");}

        PaymentTransaction pt = request.toDomain();
        GatewayResponse gatewayResponse;

        if(!circuitBreaker.allowRequest()){
            rejectAndRelease(pt, request.reservationId(), TransactionStatus.FAILED);
            throw new ServiceUnavailableException();
        }

        try {
            gatewayResponse = callGateway(request, simulateFail);
            if (gatewayResponse == null || gatewayResponse.ts() == null) {
                circuitBreaker.recordFailure();
                rejectAndRelease(pt, request.reservationId(), TransactionStatus.FAILED);
                throw new InvalidGatewayResponseException(
                        "The payment gateway returned an invalid response."
                );
            }

            circuitBreaker.recordSuccess();
            pt = fillAndSave(gatewayResponse.ts(), gatewayResponse.externalTransactionId(),LocalDateTime.now(), pt);

            if (gatewayResponse.ts() == TransactionStatus.REJECTED) {
                releaseReservation(request.reservationId());
            }

            log.info("Payment transaction processed successfully, id={}, reservationId={}, staus={}", pt.getId(), pt.getReservationId(), pt.getStatus());
            return pt;
        }catch (GatewayRetriesExhaustedException e) {
            circuitBreaker.recordFailure();
            rejectAndRelease(pt, request.reservationId(),  TransactionStatus.FAILED);
            throw e;
        }
    }

    private PaymentTransaction rejectAndRelease(
            PaymentTransaction transaction,
            UUID reservationId,
            TransactionStatus transactionStatus
    ) {
        PaymentTransaction rejected = fillAndSave(
                transactionStatus,
                null,
                LocalDateTime.now(),
                transaction
        );

        releaseReservation(reservationId);
        return rejected;
    }


    private void releaseReservation(UUID reservationId) {
        try {
            ticketingClient.releaseReservations(reservationId, PAYMENT_SERVICE_ROLE);
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
    @Value("${payment.gateway.max-attempts}")
    private int maxGatewayAttempts;
    @Value("${payment.gateway.sleep-time}")
    private int sleepTime;
    public GatewayResponse callGateway(RequestPaymentDTO request, String simulateFail){
        for (int i = 0; i<maxGatewayAttempts; i++) {
            try {
                return paymentGateway.processPayment(request.cardToken(), request.amount(), request.paymentMethod(), simulateFail);
            }catch (PaymentGatewayException | TimeoutException | ServiceUnavailableException e){
                if (i == maxGatewayAttempts - 1)  throw new GatewayRetriesExhaustedException(
                        "Payment gateway processing failed for reservation: " + request.reservationId(),
                        e
                );
                sleepRetry(sleepTime);
            }
        }
        throw new PaymentGatewayException("Gateway did not return a response");
    }


    public void sleepRetry(int milliSeconds){
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isReservationOwnedBy(UUID reservationId, UUID userId) {
        ReservationStateResponse reservation =
                ticketingClient.getReservationState(reservationId, PAYMENT_SERVICE_ROLE);

        return userId.equals(reservation.clientId());
    }

    public void validateReservationForPayment(@NotNull UUID reservationId, UUID userId, @NotNull @Positive BigDecimal paymentAmount) {
        ReservationStateResponse reservation =
                ticketingClient.getReservationState(reservationId, PAYMENT_SERVICE_ROLE);

        if (!userId.equals(reservation.clientId())) {
            throw new ForbiddenException(
                    "The reservation does not belong to the authenticated user."
            );
        }

        if (paymentAmount.compareTo(reservation.totalPrice()) != 0) {
            throw new InvalidInputException(
                    "Payment amount does not match the reservation total.",
                    Map.of("amount", "Amount must match the reservation total.")
            );
        }

    }
}
