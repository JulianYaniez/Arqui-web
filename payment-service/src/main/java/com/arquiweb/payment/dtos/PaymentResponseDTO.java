package com.arquiweb.payment.dtos;

import com.arquiweb.payment.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponseDTO(
        UUID id,
        UUID reservationId,
        TransactionStatus status,
        BigDecimal amount

) {
}
