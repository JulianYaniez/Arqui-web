package com.arquiweb.catalog.events.dto.response;

import com.arquiweb.catalog.events.domain.EventSector;

import java.math.BigDecimal;

public record EventSectorResponse(
    String sectorId,
    String name,
    boolean isNumbered,
    Integer capacity,
    Integer rows,
    Integer seatsPerRow,
    BigDecimal price
) {

    public static EventSectorResponse fromDomain(EventSector eventSector) {
        return new EventSectorResponse(
                eventSector.getSectorId(),
                eventSector.getName(),
                eventSector.isNumbered(),
                eventSector.getCapacity(),
                eventSector.getRows(),
                eventSector.getSeatsPerRow(),
                eventSector.getPrice()
        );
    }

}
