package com.arquiweb.catalog.venues.dto.response;

import com.arquiweb.catalog.venues.domain.Sector;

public record SectorResponse(
        String id,
        String name,
        boolean isNumbered,
        Integer capacity,
        Integer rows,
        Integer seatsPerRow
) {

    public static SectorResponse fromDomain(Sector sector) {
        return new SectorResponse(
                sector.getId(),
                sector.getName(),
                sector.isNumbered(),
                sector.getCapacity(),
                sector.getRows(),
                sector.getSeatsPerRow()
        );
    }
}
