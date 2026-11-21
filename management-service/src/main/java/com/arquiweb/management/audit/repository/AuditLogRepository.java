package com.arquiweb.management.audit.repository;

import com.arquiweb.management.audit.domain.AuditLog;
import com.arquiweb.management.audit.domain.enums.AuditActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findAllByOrderByCreatedAtDesc();
    List<AuditLog> findByActionTypeOrderByCreatedAtDesc(AuditActionType actionType);
    List<AuditLog> findByTargetEntityIdOrderByCreatedAtDesc(String targetEntityId);
    List<AuditLog> findByActionTypeAndTargetEntityIdOrderByCreatedAtDesc(AuditActionType actionType, String targetEntityId);
}