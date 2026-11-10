package com.arquiweb.ticketing.reservation.dto.responses;

import java.util.UUID;

public record TicketQrResponse(
        UUID ticketId,
        String qrImage
) {
}
