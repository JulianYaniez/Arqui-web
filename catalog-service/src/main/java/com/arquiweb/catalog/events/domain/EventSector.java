package com.arquiweb.catalog.events.domain;

import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;
import com.arquiweb.catalog.venues.domain.Sector;
import com.arquiweb.catalog.venues.dto.response.SectorResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EventSector {

    String sectorId;
    String name;
    boolean isNumbered;
    Integer capacity;
    Integer rows;
    Integer seatsPerRow;
    BigDecimal price;

    public EventSector(SectorResponse sector, BigDecimal price) {
        Map<String, String> errors = new HashMap<>();

        // Sector comes from DB -> Trust Sector is pre-validated.
        if (sector == null) errors.put("sector", "Event sector must refer to an existing venue sector.");

        if (price == null) errors.put("price", "Event sector must have a defined price.");
        if (price != null && price.compareTo(BigDecimal.ZERO) == 0) errors.put("price", "Event sector price cannot be lower than $0.");

        if (!errors.isEmpty()) throw new InvalidInputException("Given event sector has invalid field/s", errors);

        this.sectorId = sector.id();
        this.name = sector.name();
        this.isNumbered = sector.isNumbered();
        this.capacity = sector.capacity();
        this.rows = sector.rows();
        this.seatsPerRow = sector.seatsPerRow();
        this.price = price;
    }

}
