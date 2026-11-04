package com.arquiweb.ticketing.reservation.domain;

import com.arquiweb.ticketing.infrastructure.exception.InvalidInputException;
import com.arquiweb.ticketing.inventory.domain.Ticket;
import com.arquiweb.ticketing.inventory.domain.enums.TicketStatus;
import com.arquiweb.ticketing.reservation.domain.enums.ReservationStatus;
import com.arquiweb.ticketing.reservation.domain.exceptions.IllegalReservationStateChangeException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "payment_id")
    private UUID paymentId;

    @OneToMany(mappedBy = "reservation", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Ticket> tickets = new HashSet<>();

    public Reservation(
            UUID clientId,
            String eventId,
            BigDecimal totalAmount,
            List<Ticket> tickets,
            int ttl
    ) {
        Map<String, String> errors = new HashMap<>();

        if (clientId == null) errors.put("clientId", "Client ID cannot be null.");
        if (eventId == null || eventId.isBlank()) errors.put("eventId", "Event ID cannot be blank.");
        if (totalAmount == null) errors.put("totalAmount", "Total amount cannot be null.");
        if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) < 0) errors.put("totalAmount", "Total amount cannot be negative.");
        if (tickets == null || tickets.isEmpty()) errors.put("tickets", "Tickets cannot be null or empty.");

        if (!errors.isEmpty()) throw new InvalidInputException("Given reservation has invalid field/s.", errors);

        this.clientId = clientId;
        this.eventId = eventId;
        this.status = ReservationStatus.PENDING;
        this.totalAmount = totalAmount;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(10);

        for (Ticket ticket : tickets) {
            ticket.setReservation(this);
            this.tickets.add(ticket);
        }
    }

    public void requestRefund() {
        if (!this.getStatus().equals(ReservationStatus.PAID)) throw new IllegalReservationStateChangeException("Only reservations on a PAID state can request refunds.");

        this.setStatus(ReservationStatus.REFUND_REQUEST);
    }

    public void refund() {
        if (!this.getStatus().equals(ReservationStatus.REFUND_REQUEST)) throw new IllegalReservationStateChangeException("Only reservations pending refunds can be refunded.");

        for (Ticket ticket : this.getTickets()) {
            ticket.refund();
        }

        this.status = ReservationStatus.REFUNDED;
    }

    public void rejectRefund() {
        if (!this.getStatus().equals(ReservationStatus.REFUND_REQUEST)) throw new IllegalReservationStateChangeException("Only reservations pending refunds can be rejected.");

        this.status = ReservationStatus.PAID;
    }
}