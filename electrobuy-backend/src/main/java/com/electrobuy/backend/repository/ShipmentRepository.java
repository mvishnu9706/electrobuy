package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.Order;
import com.electrobuy.backend.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByOrder(Order order);
}