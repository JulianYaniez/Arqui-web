package com.arquiweb.management.audit.service;

import com.arquiweb.management.audit.domain.AuditLog;
import com.arquiweb.management.audit.domain.enums.AuditActionType;
import com.arquiweb.management.audit.dto.responses.AuditLogResponse;
import com.arquiweb.management.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(AuditActionType actionType, String targetEntityId, UUID adminId, String details) {
        auditLogRepository.save(new AuditLog(actionType, targetEntityId, adminId, details));
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getLogs(AuditActionType actionType, String targetEntityId) {
        boolean hasAction = actionType != null;
        boolean hasTarget = targetEntityId != null && !targetEntityId.isBlank();

        List<AuditLog> logs;
        if (hasAction && hasTarget) {
            logs = auditLogRepository.findByActionTypeAndTargetEntityIdOrderByCreatedAtDesc(actionType, targetEntityId);
        } else if (hasAction) {
            logs = auditLogRepository.findByActionTypeOrderByCreatedAtDesc(actionType);
        } else if (hasTarget) {
            logs = auditLogRepository.findByTargetEntityIdOrderByCreatedAtDesc(targetEntityId);
        } else {
            logs = auditLogRepository.findAllByOrderByCreatedAtDesc();
        }

        return logs.stream().map(AuditLogResponse::fromDomain).toList();
    }

}
