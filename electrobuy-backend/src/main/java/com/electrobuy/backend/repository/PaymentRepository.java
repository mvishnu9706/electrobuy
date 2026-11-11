package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.Order;
import com.electrobuy.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find a payment by looking up the order it belongs to
    Optional<Payment> findByOrder(Order order);

    // Find a payment using the bank's transaction ID (vital for webhooks/refunds)
    Optional<Payment> findByTransactionId(String transactionId);
}