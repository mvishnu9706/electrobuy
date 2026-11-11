package com.electrobuy.backend.controller;

import com.electrobuy.backend.dto.*;
import com.electrobuy.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 🆕 REGISTER NEW USER
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * 🔐 LOGIN EXISTING USER
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        try {
            return ResponseEntity.ok(authService.authenticate(request));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(new AuthResponse("Invalid credentials!"));
        }
    }

    /**
     * ✉️ STEP 1: REQUEST PASSWORD RESET
     * Public endpoint that triggers token generation and console-logs the recovery
     * link.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            String message = authService.generatePasswordResetToken(request.getEmail());
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            // Standardizing security response layout to prevent account harvesting
            return ResponseEntity.ok("If this email exists in our system, a reset link has been sent.");
        }
    }

    /**
     * 🔑 STEP 2: FINALIZE PASSWORD RECOVERY
     * Consumes the temporary token, validates expiration, and overwrites DB
     * records.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            String message = authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}