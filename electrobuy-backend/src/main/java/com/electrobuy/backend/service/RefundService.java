package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.Refund;

public interface RefundService {

    // Initiates a refund through Razorpay and logs it in the database
    Refund initiateRefund(long orderId, String reason);
}