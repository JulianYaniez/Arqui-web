package com.arquiweb.payment.transaction.repositori.jpa;

import com.arquiweb.payment.transaction.domain.PaymentTransaction;
import com.arquiweb.payment.transaction.domain.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentjpaRepository extends JpaRepository<PaymentTransaction, UUID> {

    boolean existsByReservationIdAndStatus(UUID reservationId, TransactionStatus status);
}
