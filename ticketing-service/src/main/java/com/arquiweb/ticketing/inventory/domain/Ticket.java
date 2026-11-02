package com.arquiweb.ticketing.inventory.domain;

import com.arquiweb.ticketing.infrastructure.exception.InvalidInputException;
import com.arquiweb.ticketing.inventory.domain.enums.TicketStatus;
import com.arquiweb.ticketing.inventory.domain.exceptions.IllegalTicketStateChangeException;
import com.arquiweb.ticketing.reservation.domain.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "ticket", uniqueConstraints = {
    @UniqueConstraint(name = "uk_event_sector_seat", columnNames = {"event_id", "sector_id", "row_number", "seat_number"})
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "sector_id", nullable = false)
    private String sectorId;

    @Column(name = "is_numbered", nullable = false)
    private boolean isNumbered;

    @Column(name = "row_number")
    private Integer rowNumber;

    @Column(name = "seat_number")
    private Integer seatNumber;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(name = "qr_token")
    private String qrToken;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "reservation_id", nullable = true)
    private Reservation reservation;

    public Ticket(  // Pre-Charge Numbered Tickets
            String eventId,
            String sectorId,
            int rowNumber,
            int seatNumber,
            BigDecimal price
    ) {
        Map<String, String> errors = new HashMap<>();

        if (eventId == null || eventId.isBlank()) errors.put("eventId", "Event ID cannot be null or blank.");
        if (sectorId == null || sectorId.isBlank()) errors.put("sectorId", "Sector ID cannot be blank.");
        if (price == null) errors.put("price", "Price cannot be null.");
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) errors.put("price", "Price cannot be negative.");

        if (!errors.isEmpty()) throw new InvalidInputException("Given ticket has invalid field/s.", errors);

        this.eventId = eventId;
        this.sectorId = sectorId;
        this.isNumbered = true;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = TicketStatus.AVAILABLE;
    }

    public Ticket( // Standing Sectors (On demand)
            String eventId,
            String sectorId,
            BigDecimal price
    ) {
        Map<String, String> errors = new HashMap<>();

        if (eventId == null || eventId.isBlank()) errors.put("eventId", "Event ID cannot be null or blank.");
        if (sectorId == null || sectorId.isBlank()) errors.put("sectorId", "Sector ID cannot be blank.");
        if (price == null) errors.put("price", "Price cannot be null.");
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) errors.put("price", "Price cannot be negative.");

        if (!errors.isEmpty()) throw new InvalidInputException("Given ticket has invalid field/s.", errors);

        this.eventId = eventId;
        this.sectorId = sectorId;
        this.isNumbered = false;
        this.price = price;
        this.status = TicketStatus.LOCKED_TEMPORARY;
    }

    public void lockTemporary() {
        if (this.status != TicketStatus.AVAILABLE) throw new IllegalTicketStateChangeException("Only available tickets can be locked.");

        this.status = TicketStatus.LOCKED_TEMPORARY;
    }

}
