package com.electrobuy.backend.dto;

// The outbound payload sent to the 3PL provider
public record ShippingRequest(
        String orderId,
        String customerName,
        String deliveryAddress,
        String pinCode,
        double weightInKg,
        double codAmount // 0 if prepaid via Razorpay
) {
}