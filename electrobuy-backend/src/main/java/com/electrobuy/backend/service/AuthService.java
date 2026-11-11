package com.electrobuy.backend.service;

import com.electrobuy.backend.dto.*;
import com.electrobuy.backend.entity.*;
import com.electrobuy.backend.repository.*;
import com.electrobuy.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository tokenRepository;

    // 🚀 INTEGRATED AUDITING DIAL
    private final AuditLogService auditLogService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered!");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);

        userRepository.save(user);

        // 🚀 AUDIT EVENT
        auditLogService.logExplicitAction(
                user.getEmail(),
                user.getRole().toString(),
                "USER_REGISTRATION",
                "Successfully provisioned account for new profile: " + user.getFullName());

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🚀 AUDIT EVENT
        auditLogService.logExplicitAction(
                user.getEmail(),
                user.getRole().toString(),
                "LOGIN_SUCCESS",
                "User successfully validated credentials and logged in.");

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    public String generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("If this email exists, a reset link has been sent."));

        tokenRepository.findByUser(user).ifPresent(existingToken -> {
            tokenRepository.delete(java.util.Objects.requireNonNull(existingToken));
        });

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);

        // 🚀 AUDIT EVENT
        auditLogService.logExplicitAction(
                user.getEmail(),
                user.getRole().toString(),
                "PASSWORD_RESET_REQUESTED",
                "Generated unique recovery link token sequence for credential override verification.");

        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        System.out.println("\n==============================================");
        System.out.println("📩 SIMULATED EMAIL TO: " + email);
        System.out.println("To reset your password, please click the link below:");
        System.out.println(resetLink);
        System.out.println("This link will expire in 15 minutes.");
        System.out.println("==============================================\n");

        return "If this email exists in our system, a reset link has been sent.";
    }

    public String resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token."));

        if (resetToken.getExpiryDate().before(new Date())) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("This reset link has expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);

        // 🚀 AUDIT EVENT
        auditLogService.logExplicitAction(
                user.getEmail(),
                user.getRole().toString(),
                "PASSWORD_RESET_SUCCESS",
                "User completely initialized and finalized new hashed account password layout.");

        return "Password successfully reset! You can now log in.";
    }
}