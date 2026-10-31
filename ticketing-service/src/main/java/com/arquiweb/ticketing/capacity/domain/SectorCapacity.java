package com.arquiweb.ticketing.capacity.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "sector_capacity")
public class SectorCapacity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "sector_id", nullable = false)
    private String sectorId;

    @Column(name = "total_capacity", nullable = false)
    private int totalCapacity;

    @Column(name = "available_capacity", nullable = false)
    private int availableCapacity;

    @Version
    @Column(name = "version")
    private Long version;

}