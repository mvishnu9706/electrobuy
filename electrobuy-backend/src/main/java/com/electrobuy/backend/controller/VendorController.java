package com.electrobuy.backend.controller;

import com.electrobuy.backend.entity.Vendor;
import com.electrobuy.backend.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendor")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor // Automatically generates high-performance immutable constructor injection
public class VendorController {

    // Field marked final to eliminate IDE linting warnings and achieve absolute
    // null safety
    private final VendorService vendorService;

    /**
     * 1. Fetch live storefront profile configurations by User Login account ID
     * URL: GET http://localhost:8085/api/vendor/{userId}/store-profile
     */
    @GetMapping("/{userId}/store-profile")
    public ResponseEntity<?> getVendorStoreProfile(@PathVariable long userId) {
        try {
            Vendor vendor = vendorService.getStoreByUserId(userId);
            return ResponseEntity.ok(vendor);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * 2. Let approved merchants modify their public business trading name details
     * URL: PUT
     * http://localhost:8085/api/vendor/{userId}/store-profile/update?newBusinessName=NewName
     */
    @PutMapping("/{userId}/store-profile/update")
    public ResponseEntity<?> updateStoreSettings(
            @PathVariable long userId,
            @RequestParam String newBusinessName) {
        try {
            Vendor updatedVendor = vendorService.updateStoreSettings(userId, newBusinessName);
            return ResponseEntity.ok(updatedVendor);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Access Blocked")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}