package com.arquiweb.ticketing.capacity.dto.requests;

import java.util.List;

public record CreateCapacityRequest(
        String eventId,
        List<CreateSectorCapacityRequest> capacities
) {
}
