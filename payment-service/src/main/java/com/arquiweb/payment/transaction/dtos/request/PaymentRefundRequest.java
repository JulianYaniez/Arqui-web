package com.arquiweb.payment.transaction.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRefundRequest(
        @NotNull UUID reservationId,
        @NotNull UUID paymentId,
        @NotNull @Positive BigDecimal totalAmound
){

}
