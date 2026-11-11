package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.Order;
import com.electrobuy.backend.entity.Shipment;
import com.electrobuy.backend.repository.OrderRepository;
import com.electrobuy.backend.repository.ShipmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Business logic layer managing logistics dispatch, state transitions,
 * and background tracking for fulfilled orders.
 */
@Service
public class FulfillmentService {

    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    private final AuditLogService auditLogService;

    public FulfillmentService(
            OrderRepository orderRepository,
            ShipmentRepository shipmentRepository,
            AuditLogService auditLogService) {
        this.orderRepository = orderRepository;
        this.shipmentRepository = shipmentRepository;
        this.auditLogService = auditLogService;
    }

    /**
     * Fetch all existing orders.
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Fetch a specific order by its ID.
     */
    public Order getOrderById(long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

    /**
     * Transition a PROCESSING order to SHIPPED and create a Shipment tracker
     * record.
     */
    @Transactional
    public Order shipOrder(long orderId, String carrier, String trackingNumber) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        if (!"PROCESSING".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Only PROCESSING orders can be shipped. Current status: " + order.getStatus());
        }

        order.setStatus("SHIPPED");
        Order savedOrder = orderRepository.save(order);

        // Populate Shipment record
        Shipment shipment = new Shipment();
        shipment.setOrder(savedOrder);
        shipment.setCourierPartner(carrier);
        shipment.setTrackingNumber(trackingNumber);
        shipment.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(5)); // Est. 5-day delivery window
        shipmentRepository.save(shipment);

        auditLogService.logAction(
                "ORDER_SHIPPED",
                "Order ID " + orderId + " has been handed over to " + carrier + " (Tracking No: " + trackingNumber
                        + ").");

        return savedOrder;
    }

    /**
     * Finalize the shipment loop by transitioning a SHIPPED order to DELIVERED.
     */
    @Transactional
    public Order deliverOrder(long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        if (!"SHIPPED".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Only SHIPPED orders can be delivered. Current status: " + order.getStatus());
        }

        order.setStatus("DELIVERED");
        Order savedOrder = orderRepository.save(order);

        // Update Shipment actual delivery date
        shipmentRepository.findByOrder(savedOrder).ifPresent(shipment -> {
            shipment.setActualDeliveryDate(LocalDateTime.now());
            shipmentRepository.save(shipment);
        });

        auditLogService.logAction(
                "ORDER_DELIVERED",
                "Order ID " + orderId + " has been successfully delivered to the customer address.");

        return savedOrder;
    }
}