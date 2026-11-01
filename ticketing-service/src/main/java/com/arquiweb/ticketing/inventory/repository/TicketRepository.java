package com.arquiweb.ticketing.inventory.repository;

import com.arquiweb.ticketing.inventory.domain.Ticket;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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
        SET t.status = 'AVAILABLE', t.reservation = null 
        WHERE t.reservation.id IN :reservations
        """)
    void unlockTickets(@Param("reservations") List<UUID> reservations);
}