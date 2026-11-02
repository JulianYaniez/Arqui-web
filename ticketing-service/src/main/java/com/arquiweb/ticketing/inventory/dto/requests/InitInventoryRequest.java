package com.arquiweb.ticketing.inventory.dto.requests;

import java.util.List;

public record InitInventoryRequest(
        String eventId,
        List<SectorCapacityDto> capacities,
        List<SectorSeatsDto> seats
) {
}
