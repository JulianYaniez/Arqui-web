package com.arquiweb.payment.dtos;

import com.arquiweb.payment.entity.PaymentTransaction;
import com.arquiweb.payment.enums.PaymentMethod;
import com.arquiweb.payment.enums.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


    public record SavePaymentDTO(
            @NotNull UUID reservationId,
            @NotNull @Positive BigDecimal amount,
            @NotNull PaymentMethod paymentMethod,
            @NotNull String cardToken
        ){

        public PaymentTransaction toDomain(){
            return new PaymentTransaction(reservationId, amount,  paymentMethod, TransactionStatus.PENDING,null, LocalDateTime.now(), null);
        }
    }


