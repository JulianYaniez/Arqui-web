package com.arquiweb.ticketing.inventory.dto.responses;

import com.arquiweb.ticketing.inventory.domain.Ticket;
import com.arquiweb.ticketing.inventory.domain.enums.TicketStatus;

import java.util.UUID;

public record SeatResponse(
        UUID id,
        int seatNumber,
        TicketStatus status
) {

    public static SeatResponse fromDomain(Ticket ticket) {
        return  new SeatResponse(
                ticket.getId(),
                ticket.getSeatNumber(),
                ticket.getStatus()
        );
    }

}
