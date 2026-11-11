package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.Vendor;
import com.electrobuy.backend.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // Automatically handles high-performance immutable constructor injection
public class VendorService {

    // Field marked final to eliminate IDE linting warnings and achieve absolute
    // null type-safety
    private final VendorRepository vendorRepository;

    /**
     * Fetch active storefront details using the User profile ID.
     */
    public Vendor getStoreByUserId(long userId) {
        return vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Store not found for user ID: " + userId));
    }

    /**
     * Let approved merchants modify their public business trading name details.
     */
    @Transactional

    public Vendor updateStoreSettings(long userId, String newBusinessName) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new RuntimeException("Modification rejected: Active merchant registration not found."));

        // Safety Gatekeeper check: Verify if the administrator has suspended this
        // storefront
        if (!vendor.isVendorActive()) {
            throw new RuntimeException(
                    "Access Blocked: Your storefront profile has been suspended by system administration control.");
        }

        // Update name and flush to DB
        vendor.setBusinessName(newBusinessName);
        return vendorRepository.save(vendor);
    }
}