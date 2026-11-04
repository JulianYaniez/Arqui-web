package com.arquiweb.management.infrastructure.client.dto.responses;

import com.arquiweb.management.infrastructure.client.dto.requests.ReservationStateResponse;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRefundRequest(
        UUID reservationId,
        UUID paymentId,
        BigDecimal totalAmound
){

    public static PaymentRefundRequest fromReservation(ReservationStateResponse reservation) {
        return new PaymentRefundRequest(
                reservation.reservationId(),
                reservation.paymentId(),
                reservation.totalPrice()
        );
    }

}
