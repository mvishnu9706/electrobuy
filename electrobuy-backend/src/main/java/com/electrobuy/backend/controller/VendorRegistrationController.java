package com.electrobuy.backend.controller;

import com.electrobuy.backend.dto.VendorRegistrationDto;
import com.electrobuy.backend.service.VendorRegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/vendor-onboarding")
@CrossOrigin(origins = "*") // Allows your front-end vendor panel to submit documents smoothly
@RequiredArgsConstructor // Automatically generates high-performance immutable constructor injection
public class VendorRegistrationController {

    // Fields marked final to eliminate IDE linting warnings and achieve absolute
    // null safety
    private final VendorRegistrationService vendorRegistrationService;
    private final ObjectMapper objectMapper; // Cleanly handles JSON string-to-DTO conversion

    /**
     * Handles the unified Seller Hub registration submission form.
     * Consumes multi-part data containing JSON metadata text and a physical PDF
     * file.
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerVendor(
            @RequestPart("vendorData") String vendorDataJson,
            @RequestPart("pdfDocument") MultipartFile file) {

        try {
            // Convert incoming text string JSON payload into data structure DTO mapping
            // object smoothly
            VendorRegistrationDto dto = objectMapper.readValue(vendorDataJson, VendorRegistrationDto.class);

            // Execute registration logic safely via the optimized business logic pipeline
            vendorRegistrationService.registerNewVendor(dto, file);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Vendor registration submitted successfully! Awaiting administrator document verification.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration processing error occurred: " + e.getMessage());
        }
    }
}