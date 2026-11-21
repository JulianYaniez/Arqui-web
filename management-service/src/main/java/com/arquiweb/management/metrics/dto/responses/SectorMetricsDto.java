package com.arquiweb.management.metrics.dto.responses;

import com.arquiweb.management.metrics.domain.jsonb.SectorMetrics;

import java.math.BigDecimal;

public record SectorMetricsDto(
        String sectorId,
        long capacity,
        long soldTickets,
        long refundedTickets,
        BigDecimal revenue
) {

    public static SectorMetricsDto fromDomain(SectorMetrics sm) {
        return new SectorMetricsDto(
                sm.sectorId(),
                sm.capacity(),
                sm.soldTickets(),
                sm.refundedTickets(),
                sm.revenue()
        );
    }

}
