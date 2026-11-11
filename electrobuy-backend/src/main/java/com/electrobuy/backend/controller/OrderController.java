package com.electrobuy.backend.controller;

import com.electrobuy.backend.dto.OrderAndPaymentResponse;
import com.electrobuy.backend.entity.Order;
import com.electrobuy.backend.service.PaymentOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class exposing API endpoints for managing shopping orders and
 * payments.
 * Synchronized to consume PaymentOrderService and produce
 * OrderAndPaymentResponse packets.
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OrderController {

    // Inject the updated PaymentOrderService implementation
    private final PaymentOrderService paymentOrderService;

    /**
     * Endpoint to finalize a checkout transaction from a user's active shopping
     * cart items.
     * Invokes secure third-party billing registration and initializes local order
     * data.
     * URL: POST http://localhost:8085/api/orders/place?userId=1&addressId=2
     */
    @PostMapping("/place")
    public ResponseEntity<OrderAndPaymentResponse> placeOrder(
            @RequestParam long userId,
            @RequestParam long addressId) {
        OrderAndPaymentResponse response = paymentOrderService.placeOrder(userId, addressId);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to fetch the entire purchase logs and invoice history matching a
     * customer.
     * URL: GET http://localhost:8085/api/orders/user/1
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getMyOrders(@PathVariable long userId) {
        return ResponseEntity.ok(paymentOrderService.getUserOrders(userId));
    }

    /**
     * Endpoint to view a single specific transaction detail card view using its ID.
     * URL: GET http://localhost:8085/api/orders/5
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderDetails(@PathVariable long orderId) {
        Order order = paymentOrderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }
}