package com.arquiweb.catalog.venues.dto.query;

import com.arquiweb.catalog.venues.domain.Location;
import com.arquiweb.catalog.venues.domain.Venue;

import java.util.List;

public record VenueQuery(
        String id,
        String name,
        String city,
        Location location,
        List<SectorQuery> sectors
) {

    public static VenueQuery fromDomain(Venue venue) {
        return new VenueQuery(
                venue.getId(),
                venue.getName(),
                venue.getCity(),
                venue.getLocation(),
                venue.getSectors().stream()
                        .map(SectorQuery::fromDomain)
                        .toList()
        );
    }

}
