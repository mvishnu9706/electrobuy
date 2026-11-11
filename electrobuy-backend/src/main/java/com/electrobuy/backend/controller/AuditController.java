package com.electrobuy.backend.controller;

import com.electrobuy.backend.entity.AuditLog;
import com.electrobuy.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    /**
     * 🕵️‍♂️ GET STAFF LOGINS
     * Fetches every time an Admin or Vendor logged into the system.
     * URL: GET http://localhost:8085/api/audit/staff-logins
     */
    @GetMapping("/staff-logins")
    public ResponseEntity<List<AuditLog>> getStaffLogins() {
        // We only want to see people with elevated permissions, ignore CUSTOMER
        List<String> staffRoles = Arrays.asList("ROLE_ADMIN", "ROLE_VENDOR");

        List<AuditLog> staffLogins = auditLogRepository
                .findByUserRoleInAndActionTypeOrderByTimestampDesc(staffRoles, "LOGIN_SUCCESS");

        return ResponseEntity.ok(staffLogins);
    }
}