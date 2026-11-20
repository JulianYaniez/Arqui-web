package com.arquiweb.ticketing.inventory.dto.requests;

import java.math.BigDecimal;

public record SectorMetricsDto(
        String sectorId,
        Long capacity,
        Long soldTickets,
        Long refundedTickets,
        BigDecimal revenue
) {
}
