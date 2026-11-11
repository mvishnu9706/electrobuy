package com.electrobuy.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity tracking financial payment returns for cancelled or returned orders.
 */
@Entity
@Table(name = "refunds")
@Data
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "razorpay_refund_id", unique = true, length = 255)
    private String razorpayRefundId;

    @Column(nullable = false)
    private double refundAmount;

    @Column(nullable = false, length = 50)
    private String status; // PENDING, PROCESSED, FAILED

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}