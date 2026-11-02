package com.arquiweb.ticketing.reservation.dto.requests;

public record ReserveUnnumberedRequest(
        String eventId,
        String sectorId,
        Integer ticketQuantity
) {
}
