package com.arquiweb.catalog.venues.dto.query;

import com.arquiweb.catalog.venues.domain.Sector;

public record SectorQuery(
        String id,
        String name,
        boolean isNumbered,
        Integer capacity,
        Integer rows,
        Integer seatsPerRow
) {

    public static SectorQuery fromDomain(Sector sector) {
        return new SectorQuery(
                sector.getId(),
                sector.getName(),
                sector.isNumbered(),
                sector.getCapacity(),
                sector.getRows(),
                sector.getSeatsPerRow()
        );
    }
}
