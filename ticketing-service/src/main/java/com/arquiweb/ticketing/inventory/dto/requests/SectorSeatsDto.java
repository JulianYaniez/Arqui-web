package com.arquiweb.ticketing.inventory.dto.requests;

import com.arquiweb.ticketing.inventory.domain.Ticket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record SectorSeatsDto(
        String sectorId,
        int rows,
        int seatsPerRow,
        BigDecimal price
) {

    public List<Ticket> toTickets(String eventId) {
        List<Ticket> tickets = new ArrayList<>();


        for (int r = 1; r <= this.rows(); r++) {
            for (int s = 1; s <= this.seatsPerRow(); s++) {
                tickets.add(new Ticket(
                        eventId,
                        this.sectorId(),
                        r,
                        s,
                        this.price()
                ));
            }
        }

        return  tickets;
    }

}
