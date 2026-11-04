package com.arquiweb.ticketing.reservation.repository;

import com.arquiweb.ticketing.reservation.domain.Reservation;
import com.arquiweb.ticketing.reservation.domain.enums.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query("""
        SELECT COUNT(t)
        FROM Reservation r JOIN r.tickets t
        WHERE r.clientId = :clientId AND r.eventId = :eventId
    """)
    int countTicketsByClientIdAndEventId(
            @Param("client_id") UUID clientId,
            @Param("event_id") String eventId
    );

    @Modifying
    @Query("""
        UPDATE Reservation r
        SET r.status = :status WHERE
           r.id IN :ids
        """)
    void expire(
            @Param("ids") List<UUID> ids,
            @Param("status") ReservationStatus status
    );


    @Query("""
        SELECT r.id
        FROM Reservation r WHERE
            r.status = :status AND
            r.expiresAt < :now
        """)
    List<UUID> findExpired(
            @Param("now") LocalDateTime now,
            @Param("status") ReservationStatus status
    );

    List<Reservation> findByEventIdAndStatus(String eventId, ReservationStatus status);
}