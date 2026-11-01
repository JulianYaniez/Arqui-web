package com.arquiweb.ticketing.inventory.dto.requests;

import com.arquiweb.ticketing.inventory.domain.SectorCapacity;

import java.math.BigDecimal;

public record SectorCapacityDto(
        String sectorId,
        int totalCapacity,
        BigDecimal price
    ) {

    public SectorCapacity toCapacity(String eventId) {
        return new SectorCapacity(
                eventId,
                sectorId,
                totalCapacity,
                price
        );
    }

}
