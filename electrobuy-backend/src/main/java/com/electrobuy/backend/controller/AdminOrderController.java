package com.electrobuy.backend.controller;

import com.electrobuy.backend.entity.Order;
import com.electrobuy.backend.entity.Shipment;
import com.electrobuy.backend.entity.Refund;
import com.electrobuy.backend.service.FulfillmentService;
import com.electrobuy.backend.service.LogisticsService;
import com.electrobuy.backend.service.RefundService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller exposing administrative endpoints protected by Role-Based Access
 * Controls.
 * Manages order visibility, manual/automated shipping, delivery tracking, and
 * refunds.
 */
@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyAuthority('ADMIN', 'VENDOR')") // Spring Security authority guard
public class AdminOrderController {

    private final FulfillmentService fulfillmentService;
    private final LogisticsService logisticsService;
    private final RefundService refundService;

    // Constructor Injection
    public AdminOrderController(FulfillmentService fulfillmentService,
            LogisticsService logisticsService,
            RefundService refundService) {
        this.fulfillmentService = fulfillmentService;
        this.logisticsService = logisticsService;
        this.refundService = refundService;
    }

    // ==========================================
    // ORDER VIEWING & MANAGEMENT
    // ==========================================

    /**
     * Endpoint to fetch all orders (optionally paginated in a real-world scenario).
     * URL: GET http://localhost:8085/api/admin/orders
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = fulfillmentService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Endpoint to fetch details of a specific order.
     * URL: GET http://localhost:8085/api/admin/orders/{orderId}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable long orderId) {
        Order order = fulfillmentService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    // ==========================================
    // SHIPPING & LOGISTICS (MANUAL & AUTOMATED)
    // ==========================================

    /**
     * Endpoint for MANUAL shipment tracking (Legacy/Override).
     * URL: PUT
     * http://localhost:8085/api/admin/orders/6/ship?carrier=BlueDart&trackingNumber=BD-9876543
     */
    @PutMapping("/{orderId}/ship")
    public ResponseEntity<Order> shipOrder(
            @PathVariable long orderId,
            @RequestParam String carrier,
            @RequestParam String trackingNumber) {

        Order order = fulfillmentService.shipOrder(orderId, carrier, trackingNumber);
        return ResponseEntity.ok(order);
    }

    /**
     * Endpoint for AUTOMATED 3PL API fulfillment (e.g., Shiprocket, Delhivery).
     * URL: POST http://localhost:8085/api/admin/orders/6/fulfill
     */
    @PostMapping("/{orderId}/fulfill")
    @Transactional
    public ResponseEntity<Shipment> fulfillOrderVia3PL(@PathVariable long orderId) {
        // Orchestrates the 3PL API call and updates the Shipment entity
        Shipment updatedShipment = logisticsService.processAutomatedFulfillment(orderId);
        return ResponseEntity.ok(updatedShipment);
    }

    /**
     * Endpoint to finalize delivery cycle of a shipped parcel.
     * URL: PUT http://localhost:8085/api/admin/orders/6/deliver
     */
    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<Order> deliverOrder(@PathVariable long orderId) {
        Order order = fulfillmentService.deliverOrder(orderId);
        return ResponseEntity.ok(order);
    }

    // ==========================================
    // CANCELLATIONS & REFUNDS
    // ==========================================

    /**
     * Endpoint to process a refund for a returned or cancelled order via Razorpay.
     * URL: POST http://localhost:8085/api/admin/orders/6/refund
     */
    @PostMapping("/{orderId}/refund")
    @Transactional
    public ResponseEntity<Refund> processOrderRefund(
            @PathVariable long orderId,
            @RequestParam(required = false) String reason) {

        Refund refund = refundService.initiateRefund(orderId, reason);
        return ResponseEntity.ok(refund);
    }
}