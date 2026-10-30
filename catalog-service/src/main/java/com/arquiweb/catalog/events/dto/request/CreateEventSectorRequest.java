package com.arquiweb.catalog.events.dto.request;

import com.arquiweb.catalog.events.domain.EventSector;
import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;
import com.arquiweb.catalog.venues.domain.Sector;
import com.arquiweb.catalog.venues.dto.response.SectorResponse;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public record CreateEventSectorRequest(
    String sectorId,
    BigDecimal price
) {

    public CreateEventSectorRequest {
        Map<String, String> errors = new HashMap<>();

        if (sectorId == null || sectorId.isBlank()) errors.put("sectorId", "Sector ID cannot be null or blank.");
        if (sectorId != null && ObjectId.isValid(sectorId)) errors.put("sectorId", "Sector ID must have a valid format.");

        if (!errors.isEmpty()) throw new InvalidInputException("Given sector with ID '" + sectorId + "' has invalid field/s.", errors);
    }


    public EventSector toDomain(SectorResponse sector) {
        return new EventSector(
                sector,
                this.price
        );
    }

}
