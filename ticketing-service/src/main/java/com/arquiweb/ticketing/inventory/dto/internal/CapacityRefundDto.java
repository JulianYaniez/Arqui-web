package com.arquiweb.ticketing.inventory.dto.internal;

public record CapacityRefundDto(
        String eventId,
        String sectorId,
        long count
) {
}
