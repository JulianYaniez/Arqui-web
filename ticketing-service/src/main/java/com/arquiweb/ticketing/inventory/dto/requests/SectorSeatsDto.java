package com.arquiweb.ticketing.inventory.dto.requests;

import com.arquiweb.ticketing.inventory.domain.Ticket;

import java.math.BigDecimal;

public record SectorSeatsDto(
        String sectorId,
        int rows,
        int seatsPerRow,
        BigDecimal price
) {

    public Ticket toTicket(String eventId) {
        return new Ticket(
                eventId,
                sectorId,
                rows,
                seatsPerRow,
                price
        );
    }

}
