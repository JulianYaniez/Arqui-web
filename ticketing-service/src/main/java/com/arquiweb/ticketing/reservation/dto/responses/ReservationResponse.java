package com.arquiweb.ticketing.reservation.dto.responses;

import com.arquiweb.ticketing.reservation.domain.Reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponse(
        UUID reservationId,
        UUID clientId,
        String eventId,
        BigDecimal totalPrice,
        LocalDateTime expiresAt
) {

    public static ReservationResponse fromDomain(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getClientId(),
                reservation.getEventId(),
                reservation.getTotalAmount(),
                reservation.getExpiresAt()
        );
    }

}
