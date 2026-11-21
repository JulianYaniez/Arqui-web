package com.arquiweb.management.audit.dto.responses;

import com.arquiweb.management.audit.domain.AuditLog;
import com.arquiweb.management.audit.domain.enums.AuditActionType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        AuditActionType actionType,
        String targetEntityId,
        UUID adminId,
        String details,
        LocalDateTime createdAt
) {
    public static AuditLogResponse fromDomain(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getActionType(),
                log.getTargetEntityId(),
                log.getAdminId(),
                log.getDetails(),
                log.getCreatedAt()
        );
    }
}