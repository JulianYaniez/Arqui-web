package com.arquiweb.ticketing.reservation.dto.responses;

import com.arquiweb.ticketing.inventory.domain.Ticket;

import java.util.UUID;

public record TicketResponse(
        UUID id,
        String eventId,
        String sectorId,
        boolean isNumbered,
        Integer rowNumber,
        Integer seatNumber
) {

    public static TicketResponse fromDomain(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getEventId(),
                ticket.getSectorId(),
                ticket.isNumbered(),
                ticket.getRowNumber(),
                ticket.getSeatNumber()
        );
    }
}
