package com.arquiweb.catalog.infrastructure.client.dtos.command;

import com.arquiweb.catalog.events.domain.EventSector;

import java.math.BigDecimal;

public record SectorCapacityDto(
        String sectorId,
        Integer totalCapacity,
        BigDecimal price
) {

    public static SectorCapacityDto fromDomain(EventSector eventSector) {
        return new SectorCapacityDto(
                eventSector.getSectorId(),
                eventSector.getCapacity(),
                eventSector.getPrice()
        );
    }
}
