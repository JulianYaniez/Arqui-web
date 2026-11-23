package com.arquiweb.ticketing.inventory.repository;

import com.arquiweb.ticketing.inventory.domain.Ticket;
import com.arquiweb.ticketing.inventory.domain.enums.TicketStatus;
import com.arquiweb.ticketing.inventory.dto.internal.CapacityRefundDto;
import com.arquiweb.ticketing.inventory.dto.requests.SectorMetricsDto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByEventIdAndSectorId(String eventId, String sectorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT t FROM Ticket t WHERE t.id IN :ticketIds
    """)
    List<Ticket> findByIdIn(List<UUID> ticketIds);


    @Modifying
    @Query("""
        UPDATE Ticket t
        SET t.status = :status, t.reservation = null
        WHERE t.reservation.id IN :reservations AND
                t.isNumbered = true
        """)
    void unlockNumberedTickets(
            @Param("reservations") List<UUID> reservations,
            @Param("status") TicketStatus status
            );


    @Query("""
        SELECT new com.arquiweb.ticketing.inventory.dto.internal.CapacityRefundDto(t.eventId, t.sectorId, COUNT(t))
        FROM Ticket t WHERE
                t.reservation.id IN :reservations AND
                t.isNumbered = false
        GROUP BY t.eventId, t.sectorId
        """)
    List<CapacityRefundDto> countUnnumberedCapacitiesToRefund(@Param("reservations") List<UUID> reservations);


    @Modifying
    @Query("""
        DELETE FROM Ticket t WHERE
                t.reservation.id IN :reservations AND
                t.isNumbered = false
        """)
    void deleteExpiredUnnumbered(@Param("reservations") List<UUID> reservations);

    Optional<Ticket> findByQrToken(String qrToken);

    List<Ticket> findByIsNumbered(boolean isNumbered);

    @Query("""
        SELECT t
        FROM Ticket t
        WHERE t.reservation.id IN :reservationIds
    """)
    List<Ticket> findByReservationIdIn(
            @Param("reservationIds") List<UUID> reservationIds
    );

    @Query("""
        SELECT new com.arquiweb.ticketing.inventory.dto.requests.SectorMetricsDto(
            t.sectorId,
            COUNT(t),
            COUNT(CASE WHEN t.status IN :soldStatuses THEN 1L END),
            (SELECT COUNT(r) FROM TicketRefundLog r WHERE  r.eventId = t.eventId AND r.sectorId = t.sectorId),
            CAST(COALESCE(SUM(CASE WHEN t.status IN :soldStatuses THEN t.price END), 0L) AS bigdecimal)
        )
        FROM Ticket t
        WHERE t.eventId = :eventId AND t.isNumbered = true
        GROUP BY t.eventId, t.sectorId
    """)
    List<SectorMetricsDto> findNumberedSectorStats(
            @Param("eventId") String eventId,
            @Param("soldStatuses") List<TicketStatus> soldStatuses
    );
    
    boolean existsByEventIdAndSectorIdAndRowNumberAndSeatNumber(
            String eventId,
            String sectorId,
            Integer rowNumber,
            Integer seatNumber
    );
}