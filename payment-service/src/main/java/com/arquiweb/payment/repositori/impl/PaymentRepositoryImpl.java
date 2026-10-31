package com.arquiweb.payment.repositori.impl;

import com.arquiweb.payment.entity.PaymentTransaction;
import com.arquiweb.payment.enums.TransactionStatus;
import com.arquiweb.payment.repositori.interfaces.PaymentRepository;
import com.arquiweb.payment.repositori.jpa.PaymentjpaRepository;
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
