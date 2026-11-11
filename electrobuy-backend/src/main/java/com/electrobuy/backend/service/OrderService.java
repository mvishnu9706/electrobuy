package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.*;
import com.electrobuy.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final AuditLogService auditLogService;

    // 🚀 NEW: Inject the Payment Tracking System
    private final PaymentRepository paymentRepository;

    @Transactional
    public Order placeOrder(long userId, long addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + addressId));

        List<CartItem> cartItems = cartRepository.findByUser(user);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cannot place order: Cart is empty.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(address);
        order.setStatus("PENDING");

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            ProductVariant variant = cartItem.getProductVariant();
            Inventory inventory = variant.getInventory();

            if (inventory == null || inventory.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient warehouse stock level available for this item.");
            }

            // Deduct from the specific variation allocation pool
            inventory.setStockQuantity(inventory.getStockQuantity() - cartItem.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(variant.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(variant.getPrice());

            orderItems.add(orderItem);
            totalAmount += (variant.getPrice() * cartItem.getQuantity());
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        // 1. Save the final Order
        Order savedOrder = orderRepository.save(order);

        // 2. Empty the User's Cart
        cartRepository.deleteAll(cartItems);

        // 🚀 3. NEW: Initialize the Payment Ledger Entry
        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setAmount(totalAmount);
        payment.setPaymentStatus(PaymentStatus.PENDING); // They haven't paid yet!
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD); // Defaulting for now

        paymentRepository.save(payment);

        // 🚀 AUDIT EVENT
        auditLogService.logAction(
                "ORDER_PLACED",
                "Successfully initialized order ID: " + savedOrder.getId() +
                        " containing " + orderItems.size() + " items. Grand Total: $" + totalAmount +
                        ". Pending Payment Invoice generated.");

        return savedOrder;
    }

    public List<Order> getUserOrders(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User profile registry entry not found for ID: " + userId));
        return orderRepository.findByUser(user);
    }

    public Order getOrderById(long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order invoice matching transaction ID not found: " + orderId));
    }
}