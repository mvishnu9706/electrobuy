package com.electrobuy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object containing the payment completion tokens returned by
 * the frontend Razorpay Checkout widget. Used to verify the integrity of the
 * payment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationRequest {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String signature;
    private long orderId;
}