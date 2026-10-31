package com.arquiweb.catalog.infrastructure.client.dtos.command;

import com.arquiweb.catalog.events.domain.Event;
import com.arquiweb.catalog.events.domain.EventSector;

import java.util.ArrayList;
import java.util.List;

public record InitCapacityRequest(
        String eventId,
        List<SectorCapacityDto> capacities,
        List<SectorSeatsDto> seats
) {

    public static InitCapacityRequest fromDomain(Event event) {
        List<SectorCapacityDto> capacities = new ArrayList<>();
        List<SectorSeatsDto> seats = new ArrayList<>();

        for (EventSector sector : event.getSectors()) {
            if (sector.isNumbered()) {
                seats.add(SectorSeatsDto.fromDomain(sector));
            } else {
                capacities.add(SectorCapacityDto.fromDomain(sector));
            }
        }

        return new InitCapacityRequest(
                event.getId(),
                capacities,
                seats
        );
    }

}
