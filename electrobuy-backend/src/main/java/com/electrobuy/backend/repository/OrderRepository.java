package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.Order;
import com.electrobuy.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing Order entities.
 * Synchronized with custom query methods to support background data sweeps.
 */

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Retrieves a list of orders associated with a specific customer.
     */
    List<Order> findByUser(User user);

    /**
     * Custom Query Method: Automatically translated by Spring Data JPA.
     * Finds all orders matching a specific status created prior to a given
     * expiration threshold.
     * Used by the background OrderCleanupScheduler to sweep and cancel abandoned
     * transactions.
     */
    List<Order> findByStatusAndOrderDateBefore(String status, LocalDateTime threshold);
}