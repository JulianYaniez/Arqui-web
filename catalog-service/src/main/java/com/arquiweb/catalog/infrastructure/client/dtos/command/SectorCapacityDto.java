package com.arquiweb.catalog.infrastructure.client.dtos.command;

import com.arquiweb.catalog.events.domain.EventSector;

public record SectorCapacityDto(
        String sectorId,
        Integer totalCapacity
) {

    public static SectorCapacityDto fromDomain(EventSector eventSector) {
        return new SectorCapacityDto(eventSector.getSectorId(), eventSector.getCapacity());
    }
}
