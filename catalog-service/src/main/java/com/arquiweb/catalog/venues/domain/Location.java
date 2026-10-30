package com.arquiweb.catalog.venues.domain;

import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;

import java.util.HashMap;
import java.util.Map;

public record Location(
        double latitude,
        double longitude
) {

    public Location {
        Map<String, String > errors = new HashMap<String, String>();

        if (latitude < -90.0 || latitude > 90.0) {
            errors.put("latitude", "Latitude must be between -90 and 90 degrees. Provided: " + latitude);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            errors.put("longitude", "Longitude must be between -180 and 180 degrees. Provided: " + longitude);
        }

        if (!errors.isEmpty()) {
            throw new InvalidInputException("Given location has invalid field/s.", errors);
        }
    }

}
