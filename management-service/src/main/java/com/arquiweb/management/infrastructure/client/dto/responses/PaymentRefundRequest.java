package com.arquiweb.management.infrastructure.client.dto.responses;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRefundRequest(
        UUID reservationId,
        UUID paymentId,
        BigDecimal totalAmound
){
}
