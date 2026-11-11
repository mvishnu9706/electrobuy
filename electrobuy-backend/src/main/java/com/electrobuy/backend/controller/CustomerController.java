package com.electrobuy.backend.controller;

import com.electrobuy.backend.entity.CustomerProfile;
import com.electrobuy.backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin(origins = "*") // Added cross-origin mapping to let your UI connect smoothly
@RequiredArgsConstructor // Automatically generates high-performance immutable constructor injection
public class CustomerController {

    // Field marked final to eliminate IDE linting warnings and achieve absolute
    // null safety
    private final CustomerService customerService;

    /**
     * Fetch the shopper profile settings.
     * Auto-provisions a tracking record row if it does not exist yet via the
     * service layer.
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<CustomerProfile> getProfile(@PathVariable long userId) {
        return ResponseEntity.ok(customerService.getProfileByUserId(userId));
    }

    /**
     * Update address preferences and billing configurations.
     */
    @PutMapping("/{userId}/profile/update")
    public ResponseEntity<CustomerProfile> updateProfile(
            @PathVariable long userId,
            @RequestParam String address,
            @RequestParam String defaultPayment) {

        CustomerProfile updated = customerService.updateShippingDetails(userId, address, defaultPayment);
        return ResponseEntity.ok(updated);
    }
}