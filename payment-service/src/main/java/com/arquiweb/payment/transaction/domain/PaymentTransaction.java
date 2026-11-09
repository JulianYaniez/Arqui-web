package com.arquiweb.payment.transaction.domain;

import com.arquiweb.payment.infrastructure.exception.InvalidInputException;
import com.arquiweb.payment.transaction.domain.enums.PaymentMethod;
import com.arquiweb.payment.transaction.domain.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_transaction")
@Getter
@Setter
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "reservationId", nullable = false)
    private UUID reservationId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "paymentMethod", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentmethod;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "externalGatewayTransactionId")
    private String externalGatewayTransactionId;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processedAt")
    private LocalDateTime processedAt;



    public PaymentTransaction(UUID reservationId, BigDecimal amount,
                              PaymentMethod paymentmethod, TransactionStatus status,
                              String externalGatewayTransactionId, LocalDateTime createdAt,
                              LocalDateTime processedAt){

        Map<String, String> errors = new HashMap<>();

        if(reservationId == null) errors.put("reservationId","reservationId cannot be null.");
        if(amount == null) errors.put("amount","amount cannot be null.");
        if(amount != null && amount.compareTo(BigDecimal.ZERO) < 0)errors.put("amount", "amount cannot be negative.");
        if(paymentmethod == null) errors.put("paymentMethod","paymentMethod cannot be null.");
        if(status == null) errors.put("status", "status cannot be null.");
        if(createdAt == null) errors.put("createdAt","createdAt cannot be null.");

        if(!errors.isEmpty()) throw new InvalidInputException("Given information has invalid field/s.", errors);



        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentmethod = paymentmethod;
        this.status = status;
        this.externalGatewayTransactionId = externalGatewayTransactionId;
        this.createdAt = createdAt;
        this.processedAt = processedAt;

    }
}
