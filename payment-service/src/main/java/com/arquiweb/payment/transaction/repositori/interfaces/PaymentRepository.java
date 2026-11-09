package com.arquiweb.payment.transaction.repositori.interfaces;

import com.arquiweb.payment.transaction.domain.PaymentTransaction;
import com.arquiweb.payment.transaction.domain.enums.TransactionStatus;

import java.util.Optional;
import java.util.UUID;


public interface PaymentRepository{

    Optional<PaymentTransaction> getPaymentTransactionById(UUID id);

    PaymentTransaction save(PaymentTransaction paymentTransaction);

    boolean isPaymentProcessedAndStatus(UUID reservationId, TransactionStatus status);
}
