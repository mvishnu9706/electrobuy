package com.electrobuy.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@EntityListeners(AuditingEntityListener.class) // Automatically fills in the CreatedDate
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links this payment directly to a specific Order
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // The exact amount charged
    @Column(nullable = false)
    private Double amount;

    // Restricts the value to our specific Enum list (e.g., UPI, CREDIT_CARD)
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    // Restricts the value to our specific Enum list (e.g., PENDING, COMPLETED)
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    // The unique ID given by Stripe, Razorpay, or PayPal (can be null for Cash on
    // Delivery)
    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    // Automatically records the exact millisecond the transaction was created
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}