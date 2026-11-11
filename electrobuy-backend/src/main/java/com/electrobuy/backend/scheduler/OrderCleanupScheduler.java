package com.electrobuy.backend.scheduler;

import com.electrobuy.backend.entity.*;
import com.electrobuy.backend.repository.OrderRepository;
import com.electrobuy.backend.repository.PaymentRepository;
import com.electrobuy.backend.service.AuditLogService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Background Scheduler Component that sweeps the database at configured
 * intervals.
 * Reclaims locked warehouse inventory from orders abandoned during the checkout
 * process.
 */
@Component
public class OrderCleanupScheduler {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogService auditLogService;

    // Configurable window in minutes before a PENDING order is considered abandoned
    // (Default: 15 minutes)
    @Value("${order.cleanup.timeout.minutes:15}")
    private int timeoutMinutes;

    public OrderCleanupScheduler(
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            AuditLogService auditLogService) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.auditLogService = auditLogService;
    }

    /**
     * Automatic cron job that triggers every 5 minutes to sweep the database.
     * Marks expired pending orders as CANCELLED and returns locked inventory back
     * to stock.
     */
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void cleanupAbandonedOrders() {
        LocalDateTime expirationThreshold = LocalDateTime.now().minusMinutes(timeoutMinutes);

        // Fetch all orders still stuck in PENDING state past our threshold safety
        // window
        List<Order> abandonedOrders = orderRepository.findByStatusAndOrderDateBefore("PENDING", expirationThreshold);

        if (abandonedOrders.isEmpty()) {
            return;
        }

        auditLogService.logAction(
                "CLEANUP_JOB_START",
                "Found " + abandonedOrders.size() + " abandoned pending checkout orders to release.");

        for (Order order : abandonedOrders) {
            try {
                // 1. Mark Order as CANCELLED
                order.setStatus("CANCELLED");
                orderRepository.save(order);

                // 2. Mark the associated pending payment ledger as FAILED
                paymentRepository.findByOrder(order).ifPresent(payment -> {
                    payment.setPaymentStatus(PaymentStatus.FAILED);
                    paymentRepository.save(payment);
                });

                // 3. Return locked inventory back to the warehouse shelves
                for (OrderItem item : order.getOrderItems()) {
                    Product product = item.getProduct();

                    if (product != null && product.getVariants() != null && !product.getVariants().isEmpty()) {
                        // Find the appropriate variant (traverse safely back to inventory records)
                        ProductVariant variant = product.getVariants().get(0);
                        Inventory inventory = variant.getInventory();

                        if (inventory != null) {
                            int originalStock = inventory.getStockQuantity();
                            int restoredQuantity = item.getQuantity();

                            // Re-increment the stock level inside our transaction thread
                            inventory.setStockQuantity(originalStock + restoredQuantity);

                            auditLogService.logAction(
                                    "INVENTORY_RESTORED",
                                    "Restored " + restoredQuantity + " units for variant ID: " + variant.getId() +
                                            " (Stock: " + originalStock + " -> " + inventory.getStockQuantity() + ")");
                        }
                    }
                }

                auditLogService.logAction(
                        "ORDER_ABANDON_CLEANED",
                        "Successfully rolled back abandoned Order ID: " + order.getId() + " and released held stock.");

            } catch (Exception e) {
                auditLogService.logAction(
                        "CLEANUP_JOB_ERROR",
                        "Failed to rollback abandoned Order ID: " + order.getId() + ". Error: " + e.getMessage());
            }
        }
    }
}