package com.arquiweb.payment.transaction.dtos.response;

import com.arquiweb.payment.transaction.domain.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponseDTO(
        UUID id,
        UUID reservationId,
        TransactionStatus status,
        BigDecimal amount

) {
}
