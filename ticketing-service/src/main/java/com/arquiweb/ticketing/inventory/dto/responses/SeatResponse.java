package com.arquiweb.ticketing.inventory.dto.responses;

import com.arquiweb.ticketing.inventory.domain.Ticket;
import com.arquiweb.ticketing.inventory.domain.enums.TicketStatus;

public record SeatResponse(
        int seatNumber,
        TicketStatus status
) {

    public static SeatResponse fromDomain(Ticket ticket) {
        return  new SeatResponse(
                ticket.getSeatNumber(),
                ticket.getStatus()
        );
    }

}
