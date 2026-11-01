package com.arquiweb.ticketing.inventory.domain;

import com.arquiweb.ticketing.infrastructure.exception.InvalidInputException;
import com.arquiweb.ticketing.reservation.domain.exceptions.OccupiedSpotException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "sector_capacity",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_sector_capacity",
                        columnNames = {"event_id", "sector_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Version
    @Column(name = "version")
    private Long version;

    public SectorCapacity(
            String eventId,
            String sectorId,
            int capacity,
            BigDecimal price
    ) {
        Map<String, String> errors = new HashMap<>();

        if (eventId == null || eventId.isBlank()) errors.put("eventId", "Event id cannot be null or blank");
        if (sectorId == null || sectorId.isBlank()) errors.put("sectorId", "Sector id cannot be null or blank");

        if (capacity <= 0) errors.put("capacity", "Capacity cannot be 0 or less.");

        if (price == null) errors.put("price", "Price cannot be null.");
        if (price != null && price.compareTo(BigDecimal.ZERO) <= 0) errors.put("price", "Price cannot be negative.");

        if (!errors.isEmpty()) throw new InvalidInputException("Given sector capacity has invalid field/s.", errors);

        this.eventId = eventId;
        this.sectorId = sectorId;
        this.totalCapacity = capacity;
        this.availableCapacity = capacity;
        this.price = price;
    }

    public void reduceCapacity(int amount) {
        if (this.availableCapacity - amount < 0) throw new OccupiedSpotException("Given sector has maxed it's capacity.");

        this.availableCapacity -= amount;
    }
}