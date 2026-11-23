package com.arquiweb.payment.infrastructure.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ReservationStateResponse(
        UUID reservationId,
        UUID clientId,
        String eventId,
        UUID paymentId,
        BigDecimal totalPrice,
        String status
) {}