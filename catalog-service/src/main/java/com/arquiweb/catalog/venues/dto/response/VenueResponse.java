package com.arquiweb.catalog.venues.dto.response;

import com.arquiweb.catalog.venues.domain.Location;
import com.arquiweb.catalog.venues.domain.Venue;

import java.util.List;

public record VenueResponse(
        String id,
        String name,
        String city,
        Location location,
        List<SectorResponse> sectors
) {

    public static VenueResponse fromDomain(Venue venue) {
        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getCity(),
                venue.getLocation(),
                venue.getSectors().stream()
                        .map(SectorResponse::fromDomain)
                        .toList()
        );
    }

}
