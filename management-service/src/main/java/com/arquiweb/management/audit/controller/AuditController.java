package com.arquiweb.management.audit.controller;

import com.arquiweb.management.audit.domain.enums.AuditActionType;
import com.arquiweb.management.audit.dto.responses.AuditLogResponse;
import com.arquiweb.management.audit.service.AuditService;
import com.arquiweb.management.infrastructure.exception.ForbiddenException;
import com.arquiweb.management.infrastructure.utils.AuthGuard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
@Tag(name = "Audit Log", description = "Get registers about important events occurred.")
public class AuditController {

    private final AuditService auditLogService;

    @Operation(summary = "Get audit logs with params.")
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Logs fetched successfully."
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Invalid permissions."
        )
    })
    @GetMapping("/log")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs(
            @Parameter(hidden = true)
            @RequestHeader("X-User-Role") String role,
            @RequestParam(required = false) AuditActionType actionType,
            @RequestParam(required = false) String targetEntityId
    ) {
        AuthGuard.requireRole(role, "ADMIN");

        return ResponseEntity.ok(auditLogService.getLogs(actionType, targetEntityId));
    }
}