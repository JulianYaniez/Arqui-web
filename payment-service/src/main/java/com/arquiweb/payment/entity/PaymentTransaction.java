package com.arquiweb.payment.entity;

import com.arquiweb.payment.enums.PaymentMethod;
import com.arquiweb.payment.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    @Column(name = "paymentmethod", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentmethod;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @Column(name = "externalGateweyTransactionId")
    private String externalGateweyTransactionId;
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "processedAt")
    private LocalDateTime processedAt;



    public PaymentTransaction(UUID reservationId, BigDecimal amount,
                              PaymentMethod paymentmethod, TransactionStatus status,
                              String externalGatewayTransactionId, LocalDateTime createdAt,
                              LocalDateTime processedAt){
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentmethod = paymentmethod;
        this.status = status;
        this.externalGateweyTransactionId = externalGatewayTransactionId;
        this.createdAt = createdAt;
        this.processedAt = processedAt;

    }
}
