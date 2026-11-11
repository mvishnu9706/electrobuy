package com.electrobuy.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who did it? (Can link to User ID or store email/username)
    @Column(name = "performed_by", nullable = false)
    private String performedBy;

    // What role did they have? (e.g., ADMIN, VENDOR, CUSTOMER)
    @Column(name = "user_role")
    private String userRole;

    // What kind of action? (e.g., LOGIN_SUCCESS, UPDATE_STORE, PRODUCT_DELETE)
    @Column(name = "action_type", nullable = false)
    private String actionType;

    // Detailed description of the changes made
    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}