package com.arquiweb.payment.transaction.repository.impl;

import com.arquiweb.payment.transaction.domain.PaymentTransaction;
import com.arquiweb.payment.transaction.domain.enums.TransactionStatus;
import com.arquiweb.payment.transaction.repository.interfaces.PaymentRepository;
import com.arquiweb.payment.transaction.repository.jpa.PaymentjpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;



@Repository
public class PaymentRepositoryImpl implements PaymentRepository {


    private final PaymentjpaRepository paymentjpaRepository;

    public PaymentRepositoryImpl(PaymentjpaRepository paymentjpaRepository) {
        this.paymentjpaRepository = paymentjpaRepository;
    }

    @Override
    public Optional<PaymentTransaction> getPaymentTransactionById(UUID id) {
        return paymentjpaRepository.findById(id);
    }

    @Override
    public PaymentTransaction save(PaymentTransaction paymentTransaction) {
        return paymentjpaRepository.save(paymentTransaction);
    }

    @Override
    public boolean isPaymentProcessedAndStatus(UUID reservationId, TransactionStatus status) {
        return paymentjpaRepository.existsByReservationIdAndStatus(reservationId, status);
    }

}
