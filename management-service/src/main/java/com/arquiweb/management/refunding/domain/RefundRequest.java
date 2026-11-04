package com.arquiweb.management.refunding.domain;

import com.arquiweb.management.infrastructure.exception.InvalidInputException;
import com.arquiweb.management.refunding.domain.enums.RefundStatus;
import com.arquiweb.management.refunding.domain.exceptions.InvalidRefundStatusException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "refund_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefundRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "reservation_id", nullable = false)
    private UUID reservationId;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "status", nullable = false)
    private RefundStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at", nullable = true)
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by", nullable = true)
    private UUID resolvedBy;

    public RefundRequest(
            UUID reservationId,
            UUID clientId,
            String reason
    ) {
        Map<String, String> errors = new HashMap<>();

        if (reservationId == null) errors.put("reservationId", "Reservation ID cannot be null.");
        if (clientId == null) errors.put("clientId", "Client ID cannot be null.");
        if (reason == null || reason.isBlank()) errors.put("reason", "Reason cannot be null or blank.");

        if (!errors.isEmpty()) throw new InvalidInputException("Given refund request has invalid field/s.", errors);

        this.reservationId = reservationId;
        this.clientId = clientId;
        this.reason = reason;
        this.status = RefundStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void accept(UUID adminId) {
        resolve(RefundStatus.ACCEPTED, adminId);

    }

    public void reject(UUID adminId) {
        resolve(RefundStatus.REJECTED, adminId);
    }

    private void resolve(RefundStatus status, UUID adminId) {
        if (!this.status.equals(RefundStatus.PENDING)) throw new InvalidRefundStatusException("Only pending refund requests can be accepted or rejected.");

        if (adminId == null) throw new InvalidInputException("Given admin ID is not valid.", null);

        this.resolvedAt = LocalDateTime.now();
        this.resolvedBy = adminId;
        this.status = status;
    }
}