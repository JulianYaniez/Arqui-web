package com.arquiweb.ticketing.inventory.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ticket_refund_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketRefundLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "sector_id", nullable = false)
    private String sectorId;

    @Column(name = "refunded_at", nullable = false)
    private Instant refundedAt;

    public TicketRefundLog(Ticket ticket) {
        this.eventId = ticket.getEventId();
        this.sectorId = ticket.getSectorId();
        this.refundedAt = Instant.now();
    }
}
