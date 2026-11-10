package com.arquiweb.payment.transaction.repository.jpa;

import com.arquiweb.payment.transaction.domain.PaymentTransaction;
import com.arquiweb.payment.transaction.domain.enums.TransactionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PaymentjpaRepository extends JpaRepository<PaymentTransaction, UUID> {

    boolean existsByReservationIdAndStatus(UUID reservationId, TransactionStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PaymentTransaction p WHERE p.id = :id")
    Optional<PaymentTransaction> findByIdWithPessimisticLock(@Param("id") UUID id);
}
