package com.arquiweb.ticketing.capacity.repository;

import com.arquiweb.ticketing.capacity.domain.SectorCapacity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SectorCapacityRepository extends JpaRepository<SectorCapacity, UUID> {
}