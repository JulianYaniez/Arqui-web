package com.arquiweb.catalog.infrastructure.client.dtos.command;

import com.arquiweb.catalog.events.domain.EventSector;

import java.math.BigDecimal;

public record SectorSeatsDto(
        String sectorId,
        int rows,
        int seatsPerRow,
        BigDecimal price
) {

    public static SectorSeatsDto fromDomain(EventSector eventSector) {
        return new SectorSeatsDto(
                eventSector.getSectorId(),
                eventSector.getRows(),
                eventSector.getSeatsPerRow(),
                eventSector.getPrice()
        );
    }
}
