package com.arquiweb.ticketing.inventory.repository;

import com.arquiweb.ticketing.inventory.domain.SectorCapacity;
import com.arquiweb.ticketing.inventory.domain.enums.TicketStatus;
import com.arquiweb.ticketing.inventory.dto.requests.SectorMetricsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SectorCapacityRepository extends JpaRepository<SectorCapacity, UUID> {
    Optional<SectorCapacity> findByEventIdAndSectorId(String eventId, String sectorId);

    @Modifying
    @Query("UPDATE SectorCapacity s SET s.availableCapacity = s.availableCapacity + :refundCount " +
           "WHERE s.eventId = :eventId AND s.sectorId = :sectorId")
    void refundCapacity(@Param("eventId") String eventId, @Param("sectorId") String sectorId, @Param("refundCount") Long refundCount);


    @Query("""
        SELECT new com.arquiweb.ticketing.inventory.dto.requests.SectorMetricsDto(
            sc.sectorId,
            CAST(sc.totalCapacity AS Long),
            COUNT(CASE WHEN t.status IN :soldStatuses THEN 1 END),
            (SELECT COUNT(r) FROM TicketRefundLog r WHERE r.eventId = sc.eventId AND r.sectorId = sc.sectorId),
            CAST(COALESCE(SUM(CASE WHEN t.status IN :soldStatuses THEN t.price END), 0L) AS BIGDECIMAL)
        )
        FROM SectorCapacity sc
        LEFT JOIN Ticket t ON t.eventId = sc.eventId AND t.sectorId = sc.sectorId
        WHERE sc.eventId = :eventId
        GROUP BY sc.eventId, sc.sectorId, sc.totalCapacity
    """)
    List<SectorMetricsDto> findUnnumberedSectorStats(
            @Param("eventId") String eventId,
            @Param("soldStatuses") List<TicketStatus> soldStatuses
    );
}