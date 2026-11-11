package com.electrobuy.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_profiles")
@Data
public class CustomerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shipping_address", length = 512)
    private String shippingAddress;

    @Column(name = "default_payment_method")
    private String defaultPaymentMethod;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One-to-One linkage back to the central login user row
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}