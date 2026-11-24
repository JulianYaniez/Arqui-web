package com.arquiweb.management.metrics.domain;

import com.arquiweb.management.infrastructure.exception.InvalidInputException;
import com.arquiweb.management.metrics.domain.jsonb.SectorMetrics;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "event-metrics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventMetric {

    @Id
    private String eventId;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId;

    @Column(name = "total_capacity", nullable = false)
    private long totalCapacity;

    @Column(name = "total_sold_tickets", nullable = false)
    private long totalSoldTickets;

    @Column(name = "total_refunded_tickets", nullable = false)
    private long totalRefundedTickets;

    @Column(name = "occupancy_percentage", nullable = false)
    private double occupancyPercentage;

    @Column(name = "total_revenue", nullable = false)
    private BigDecimal totalRevenue;

    @Column(name = "sector_metrics", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<SectorMetrics> sectorMetrics;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    public EventMetric(
            String eventId,
            String eventName,
            UUID organizerId,
            List<SectorMetrics> sectorMetrics
    ) {
        Map<String, String> errors = new HashMap<>();

        if (eventId == null || eventId.isBlank()) errors.put("eventId", "Event ID cannot be null or blank.");
        if (organizerId == null) errors.put("organizerId", "Organizer ID cannot be null.");
        if (eventName == null || eventName.isBlank()) errors.put("eventName", "Event name cannot be null or blank.");
        if (sectorMetrics == null || sectorMetrics.isEmpty()) errors.put("sectorsMetrics", "Sector metrics cannot be null or empty.");

        if (!errors.isEmpty()) throw new InvalidInputException("Event metrics has invalid field/s.", errors);

        this.eventId = eventId;
        this.eventName = eventName;
        this.organizerId = organizerId;
        this.updateMetrics(sectorMetrics);
    }

    public void updateMetrics(List<SectorMetrics> sectorMetrics) {
        int totalCapacity = 0;
        int totalSoldTickets = 0;
        int totalRefundedTickets = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (SectorMetrics sector : sectorMetrics) {
            totalCapacity += sector.capacity();
            totalSoldTickets += sector.soldTickets();
            totalRefundedTickets += sector.refundedTickets();
            totalRevenue = totalRevenue.add(sector.revenue());
        }

        double occupancyPercentage = ((double) totalSoldTickets / totalCapacity) * 100;

        this.totalCapacity = totalCapacity;
        this.totalSoldTickets = totalSoldTickets;
        this.totalRefundedTickets = totalRefundedTickets;
        this.occupancyPercentage = occupancyPercentage;
        this.totalRevenue = totalRevenue;
        this.sectorMetrics = sectorMetrics;

        this.updatedAt = LocalDateTime.now();
    }

}
