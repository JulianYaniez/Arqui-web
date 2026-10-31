package com.arquiweb.payment.repositori.interfaces;

import com.arquiweb.payment.entity.PaymentTransaction;
import com.arquiweb.payment.enums.TransactionStatus;

import java.util.Optional;
import java.util.UUID;


public interface PaymentRepository{

    Optional<PaymentTransaction> getPaymentTransactionById(UUID id);

    PaymentTransaction save(PaymentTransaction paymentTransaction);

    boolean isPaymentProcessedAndStatus(UUID reservationId, TransactionStatus status);
}
