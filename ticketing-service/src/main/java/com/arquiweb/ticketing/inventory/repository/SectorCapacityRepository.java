package com.arquiweb.ticketing.inventory.repository;

import com.arquiweb.ticketing.inventory.domain.SectorCapacity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SectorCapacityRepository extends JpaRepository<SectorCapacity, UUID> {
    Optional<SectorCapacity> findByEventIdAndSectorId(String eventId, String sectorId);

    @Modifying
    @Query("UPDATE SectorCapacity s SET s.availableCapacity = s.availableCapacity + :refundCount " +
           "WHERE s.eventId = :eventId AND s.sectorId = :sectorId")
    void refundCapacity(@Param("eventId") String eventId, @Param("sectorId") String sectorId, @Param("refundCount") Long refundCount);
}