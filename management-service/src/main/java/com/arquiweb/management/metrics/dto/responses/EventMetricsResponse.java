package com.arquiweb.management.metrics.dto.responses;

import com.arquiweb.management.metrics.domain.EventMetric;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public record EventMetricsResponse(
        String eventId,
        String eventName,
        long totalCapacity,
        long totalSoldTickets,
        long totalRefundedTickets,
        double occupancyPercentage,
        BigDecimal totalRevenue,
        LocalDateTime lastUpdated,
        List<SectorMetricsDto> sectors
) {

    public static EventMetricsResponse fromDomain(EventMetric em) {
        return new EventMetricsResponse(
                em.getEventId(),
                em.getEventName(),
                em.getTotalCapacity(),
                em.getTotalSoldTickets(),
                em.getTotalRefundedTickets(),
                em.getOccupancyPercentage(),
                em.getTotalRevenue(),
                em.getUpdatedAt(),
                em.getSectorMetrics().stream()
                        .map(SectorMetricsDto::fromDomain)
                        .toList()
        );
    }

}
