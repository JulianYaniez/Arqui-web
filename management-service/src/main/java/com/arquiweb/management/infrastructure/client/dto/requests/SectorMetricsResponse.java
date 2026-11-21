package com.arquiweb.management.infrastructure.client.dto.requests;

import com.arquiweb.management.metrics.domain.jsonb.SectorMetrics;

import java.math.BigDecimal;

public record SectorMetricsResponse(
        String sectorId,
        Long capacity,
        Long soldTickets,
        Long refundedTickets,
        BigDecimal revenue
) {

    public SectorMetrics toDomain() {
        return new SectorMetrics(
                sectorId,
                capacity,
                soldTickets,
                refundedTickets,
                revenue
        );
    }

}
