package com.arquiweb.ticketing.inventory.repository;

import com.arquiweb.ticketing.inventory.domain.Ticket;
import com.arquiweb.ticketing.inventory.domain.enums.TicketStatus;
import com.arquiweb.ticketing.inventory.dto.internal.CapacityRefundDto;
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
}