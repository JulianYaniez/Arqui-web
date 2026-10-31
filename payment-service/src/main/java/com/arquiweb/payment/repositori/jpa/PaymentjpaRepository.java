package com.arquiweb.payment.repositori.jpa;

import com.arquiweb.payment.entity.PaymentTransaction;
import com.arquiweb.payment.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentjpaRepository extends JpaRepository<PaymentTransaction, UUID> {

    boolean existsByReservationIdAndStatus(UUID reservationId, TransactionStatus status);
}
