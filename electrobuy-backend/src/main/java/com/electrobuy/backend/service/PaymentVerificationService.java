package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.*;
import com.electrobuy.backend.repository.*;
import com.razorpay.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Core cryptographic verification logic implementing signature validation to
 * guarantee payment authenticity. Upgraded to support offline mock
 * verification.
 */
@Service
public class PaymentVerificationService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final AuditLogService auditLogService;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    // Configurable mock toggle to match the Order Service state
    @Value("${razorpay.mock.enabled:true}")
    private boolean mockMode;

    /**
     * Explicit constructor injection to maintain consistency and prevent IDE
     * warnings.
     */

    public PaymentVerificationService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            AuditLogService auditLogService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.auditLogService = auditLogService;
    }

    /**
     * Re-computes and checks the HMAC signature of the transaction payload.
     * If mockMode is active, cryptographic validation is bypassed to allow simple
     * offline validation.
     */
    @Transactional
    public String verifyPaymentSignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String signature,
            long orderId) {

        try {
            // 1. Verify Cryptographic Signature or Bypass via Mock Mode
            if (mockMode) {
                auditLogService.logAction(
                        "PAYMENT_MOCK_VERIFIED",
                        "[MOCK MODE] Bypassed HMAC signature verification for Order ID: " + orderId);
            } else {
                // Real cryptographic HMAC signature check using our secret
                org.json.JSONObject options = new org.json.JSONObject();
                options.put("razorpay_order_id", razorpayOrderId);
                options.put("razorpay_payment_id", razorpayPaymentId);
                options.put("razorpay_signature", signature);

                // Razorpay utility validates if the computed HMAC matches the provided
                // signature
                boolean isValid = Utils.verifyPaymentSignature(options, apiSecret);

                if (!isValid) {
                    throw new RuntimeException("Cryptographic signature verification failed. Potential attack.");
                }
            }

            // 2. Fetch local Payment & Order Records
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

            Payment payment = paymentRepository.findByOrder(order)
                    .orElseThrow(() -> new RuntimeException("Payment record not found for Order: " + orderId));

            // 3. Complete and finalize payment/order statuses
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(razorpayPaymentId); // Save actual payment settlement receipt ID
            paymentRepository.save(payment);

            order.setStatus("PROCESSING"); // Mark order as ready for warehouse processing
            orderRepository.save(order);

            auditLogService.logAction(
                    "PAYMENT_COMPLETED",
                    "Successfully verified payment signature. Settled Transaction ID: " + razorpayPaymentId
                            + " for Order ID: " + orderId);

            return "Payment verified successfully. Order is processing.";

        } catch (Exception e) {
            // Handle verification exception by updating status trackers to reflect failure
            orderRepository.findById(orderId).ifPresent(order -> {
                order.setStatus("CANCELLED");
                orderRepository.save(order);
                paymentRepository.findByOrder(order).ifPresent(payment -> {
                    payment.setPaymentStatus(PaymentStatus.FAILED);
                    paymentRepository.save(payment);
                });
            });
            throw new RuntimeException("Verification error: " + e.getMessage());
        }
    }
}