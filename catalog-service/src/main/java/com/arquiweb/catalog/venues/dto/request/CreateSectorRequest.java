package com.arquiweb.catalog.venues.dto.request;

import com.arquiweb.catalog.venues.domain.Sector;

public record CreateSectorRequest(
        String name,
        boolean isNumbered,
        Integer capacity,
        Integer rows,
        Integer seatsPerRow
) {

    public Sector toDomain() {
        return new Sector(
                name,
                isNumbered,
                capacity,
                rows,
                seatsPerRow
        );
    }

}
