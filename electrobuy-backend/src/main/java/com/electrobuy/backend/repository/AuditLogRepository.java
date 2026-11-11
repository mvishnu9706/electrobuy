package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Allows admins to view audit history for a specific user
    List<AuditLog> findByPerformedByOrderByTimestampDesc(String performedBy);

    // 🚀 NEW: Finds specific actions (like Logins) but ONLY for specific roles
    // (like Admins)
    List<AuditLog> findByUserRoleInAndActionTypeOrderByTimestampDesc(List<String> roles, String actionType);
}