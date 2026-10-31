package com.arquiweb.catalog.venues.dto.request;

import com.arquiweb.catalog.venues.domain.Location;
import com.arquiweb.catalog.venues.domain.Sector;
import com.arquiweb.catalog.venues.domain.Venue;

import java.util.List;

public record CreateVenueRequest(
        String name,
        String address,
        String city,
        Location location,
        List<CreateSectorRequest> sectors
) {

    public Venue toDomain() {
        return new Venue(
                name,
                address,
                city,
                location,
                sectors.stream()
                        .map(CreateSectorRequest::toDomain)
                        .toList()
        );
    }

}
