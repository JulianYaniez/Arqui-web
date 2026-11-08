package com.arquiweb.ticketing.event.dto.requests;

import com.arquiweb.ticketing.inventory.dto.requests.SectorCapacityDto;
import com.arquiweb.ticketing.inventory.dto.requests.SectorSeatsDto;

import java.util.List;

public record InitEventRequest(
        String eventId,
        List<SectorCapacityDto> capacities,
        List<SectorSeatsDto> seats
) {
}
