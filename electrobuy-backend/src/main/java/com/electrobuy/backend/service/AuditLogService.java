package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.AuditLog;
import com.electrobuy.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * AUTOMATIC LOGGING: Automatically extracts the current logged-in user
     * and their role directly from the secure JWT token context.
     */
    public void logAction(String actionType, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String performedBy = "SYSTEM_OR_GUEST";
        String userRole = "NONE";

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            performedBy = auth.getName(); // Extracts email from JWT
            userRole = auth.getAuthorities().toString(); // Extracts roles (e.g., [ROLE_VENDOR])
        }

        saveLog(performedBy, userRole, actionType, details);
    }

    /**
     * EXPLICIT LOGGING: Used for authentication actions (like Login/Register)
     * where the security context token hasn't been completely established yet.
     */
    public void logExplicitAction(String performedBy, String userRole, String actionType, String details) {
        saveLog(performedBy, userRole, actionType, details);
    }

    private void saveLog(String performedBy, String userRole, String actionType, String details) {
        AuditLog log = new AuditLog();
        log.setPerformedBy(performedBy);
        log.setUserRole(userRole.replace("[", "").replace("]", "")); // Clean bracket text format
        log.setActionType(actionType);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(log);
    }
}