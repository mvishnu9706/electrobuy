package com.electrobuy.backend.controller;

import com.electrobuy.backend.entity.Order;
import com.electrobuy.backend.entity.Payment;
import com.electrobuy.backend.entity.PaymentStatus;
import com.electrobuy.backend.repository.OrderRepository;
import com.electrobuy.backend.repository.PaymentRepository;
import com.electrobuy.backend.service.AuditLogService;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that exposes a public endpoint to listen for real-time,
 * asynchronous event notifications directly from Razorpay's servers (Webhooks).
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentWebhookController {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final AuditLogService auditLogService;

    @Value("${razorpay.webhook.secret:mock_secret}")
    private String webhookSecret;

    @Value("${razorpay.mock.enabled:true}")
    private boolean mockMode;

    public PaymentWebhookController(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            AuditLogService auditLogService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.auditLogService = auditLogService;
    }

    /**
     * Endpoint for receiving Razorpay Webhooks silently.
     * Must be configured as a public endpoint in Spring Security filters (ignored
     * by JWT filter).
     * URL: POST http://localhost:8085/api/payments/webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        try {
            // 1. Verify Webhook Authenticity (Cryptographic Check)
            if (mockMode) {
                auditLogService.logAction(
                        "WEBHOOK_MOCK_RECEIVED",
                        "[MOCK MODE] Bypassed Webhook cryptographic check. Received Payload: " + payload);
            } else {
                // Cryptographically verify that this webhook actually came from Razorpay's IP
                boolean isValid = Utils.verifyWebhookSignature(payload, signature, webhookSecret);
                if (!isValid) {
                    auditLogService.logAction("WEBHOOK_FAILED", "Invalid webhook signature validation detected.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Signature verification failed.");
                }
            }

            // 2. Parse the payload to extract the payment details
            JSONObject json = new JSONObject(payload);
            String event = json.optString("event");

            // We only process successful order settlement triggers
            if ("order.paid".equals(event) || "payment.captured".equals(event)) {

                JSONObject entityObj = json.getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity");

                String razorpayOrderId = entityObj.optString("order_id");
                String razorpayPaymentId = entityObj.optString("id");

                // 3. Locate corresponding pending Payment in our DB using the Razorpay Order
                // Token
                Payment payment = paymentRepository.findByTransactionId(razorpayOrderId)
                        .orElseThrow(() -> new RuntimeException(
                                "Payment ledger record not found for Order token: " + razorpayOrderId));

                // 4. Safely complete the order pipeline if it's not already completed
                if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
                    payment.setPaymentStatus(PaymentStatus.COMPLETED);
                    payment.setTransactionId(razorpayPaymentId); // Update from Order ID token to clear Payment
                                                                 // settlement ID
                    paymentRepository.save(payment);

                    Order order = payment.getOrder();
                    order.setStatus("PROCESSING");
                    orderRepository.save(order);

                    auditLogService.logAction(
                            "WEBHOOK_PAYMENT_SETTLED",
                            "Webhook processed successfully. Order ID: " + order.getId() + " is now PROCESSING.");
                }
            }

            // Always respond with 200 OK immediately to Razorpay to prevent server retries
            return ResponseEntity.ok("Webhook processed successfully.");

        } catch (Exception e) {
            auditLogService.logAction("WEBHOOK_ERROR", "Error parsing incoming webhook: " + e.getMessage());
            // Return 200/202 to avoid webhook flooding from external servers
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Processed with background logs: " + e.getMessage());
        }
    }
}