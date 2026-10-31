package com.arquiweb.ticketing.inventory.repository;

import com.arquiweb.ticketing.inventory.domain.SectorCapacity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SectorCapacityRepository extends JpaRepository<SectorCapacity, UUID> {
}