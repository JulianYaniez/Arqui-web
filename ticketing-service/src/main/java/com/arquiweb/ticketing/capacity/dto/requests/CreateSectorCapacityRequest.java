package com.arquiweb.ticketing.capacity.dto.requests;

public record CreateSectorCapacityRequest(
        String eventId,
        int totalCapacity
) {
}
