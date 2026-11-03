package com.arquiweb.ticketing.reservation.dto.responses;

import com.arquiweb.ticketing.reservation.domain.Reservation;

import java.math.BigDecimal;
import java.util.UUID;

public record ReservationStateResponse(
        UUID reservationId,
        UUID clientId,
        String eventId,
        BigDecimal totalPrice,
        String status
){

    public static ReservationStateResponse fromDomain(Reservation reservation) {
        return new ReservationStateResponse(
                reservation.getId(),
                reservation.getClientId(),
                reservation.getEventId(), 
                reservation.getTotalAmount(),
                reservation.getStatus().toString()
        );
    }
}
