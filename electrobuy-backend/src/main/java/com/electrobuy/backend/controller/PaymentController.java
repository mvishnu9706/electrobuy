package com.electrobuy.backend.controller;

import com.electrobuy.backend.dto.PaymentVerificationRequest;
import com.electrobuy.backend.service.PaymentVerificationService;
import com.electrobuy.backend.service.RazorpayService; // Imported your RazorpayService
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller class that exposes endpoints for securely finalizing payment
 * procedures.
 */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentVerificationService paymentVerificationService;
    private final RazorpayService razorpayService; // Injected your Razorpay wrapper service

    /**
     * Endpoint to create a Razorpay payment order intent token.
     * URL: POST http://localhost:8085/api/payments/order
     */
    @PostMapping("/order")
    public ResponseEntity<Map<String, String>> createOrder(@RequestBody Map<String, Object> requestBody) {
        try {
            double amount = Double.parseDouble(requestBody.get("amount").toString());
            String receiptId = requestBody.get("receiptId").toString();

            // Calls your RazorpayService wrapper logic
            String razorpayOrderId = razorpayService.createRazorpayOrder(amount, receiptId);

            Map<String, String> response = new HashMap<>();
            response.put("razorpayOrderId", razorpayOrderId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to generate payment intent: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Endpoint to securely authenticate payment credentials returned by the
     * Razorpay popup.
     * URL: POST http://localhost:8085/api/payments/verify
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody PaymentVerificationRequest request) {
        try {
            String response = paymentVerificationService.verifyPaymentSignature(
                    request.getRazorpayOrderId(),
                    request.getRazorpayPaymentId(),
                    request.getSignature(),
                    request.getOrderId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Signature verification failed: " + e.getMessage());
        }
    }
}