package com.electrobuy.backend.dto;

import com.electrobuy.backend.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) containing both the localized saved order details
 * and the corresponding Razorpay Order Token to render the frontend payment
 * modal.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAndPaymentResponse {
    private Order order;
    private String razorpayOrderId;
    private double totalAmount;
}