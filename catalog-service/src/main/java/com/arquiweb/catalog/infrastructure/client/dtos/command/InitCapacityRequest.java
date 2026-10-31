package com.arquiweb.catalog.infrastructure.client.dtos.command;

import com.arquiweb.catalog.events.domain.Event;

import java.util.List;

public record InitCapacityRequest(
        String eventId,
        List<SectorCapacityDto> capacities
) {

    public static InitCapacityRequest fromDomain(Event event) {
        return new InitCapacityRequest(
                event.getId(),
                event.getSectors().stream()
                        .filter(sector -> !sector.isNumbered())
                        .map(SectorCapacityDto::fromDomain)
                        .toList()
        );
    }

}
