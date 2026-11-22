package com.arquiweb.management.audit.domain;

import com.arquiweb.management.audit.domain.enums.AuditActionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "audit_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private AuditActionType actionType;

    @Column(name = "target_entity_id", nullable = false)
    private String targetEntityId;

    @Column(name = "admin_id", nullable = false)
    private UUID adminId;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AuditLog(AuditActionType actionType, String targetEntityId, UUID adminId, String details) {
        this.actionType = actionType;
        this.targetEntityId = targetEntityId;
        this.adminId = adminId;
        this.details = details;
        this.createdAt = LocalDateTime.now();
    }


}