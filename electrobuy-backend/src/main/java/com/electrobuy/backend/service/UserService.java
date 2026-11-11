package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.User;
import com.electrobuy.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Automatically handles high-performance immutable constructor injection
public class UserService {

    // Field marked final to eliminate IDE linting warnings and achieve absolute
    // null type-safety
    private final UserRepository userRepository;

    // Initialized cleanly inline and kept immutable alongside constructor
    // configurations
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Registers a new user into the marketplace ecosystem.
     * Enforces automatic cryptographic password hashing and sets user state to
     * active.
     */
    @Transactional

    public User registerUser(User user) {
        // ✅ Safely intercepts duplicate profiles before hitting raw database constraint
        // walls
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Registration failed: A profile with this email address already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true); // Ensure new users are active
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Gathers all system users, filtering out any records that have undergone a
     * soft-delete cycle.
     */
    public List<User> getAllUsers() {
        // Only return users who are not "Soft Deleted"
        return userRepository.findAll().stream()
                .filter(user -> user.isActive())
                .collect(Collectors.toList());
    }

    /**
     * Fetches an active user profile using their unique primary identification key.
     */
    public User getUserById(long id) {
        return userRepository.findById(id)
                .filter(user -> user.isActive())
                .orElseThrow(() -> new RuntimeException("User record not found or profile has been deactivated."));
    }

    /**
     * Performs a safe soft-delete routine on a user account.
     * Flips visibility status flags to preserve downstream transactional integrity
     * (orders, invoices, logs).
     */
    @Transactional

    public void deleteUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(false);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}