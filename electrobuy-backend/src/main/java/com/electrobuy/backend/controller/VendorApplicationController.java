package com.electrobuy.backend.controller;

import com.electrobuy.backend.entity.VendorApplication;
import com.electrobuy.backend.entity.User;
import com.electrobuy.backend.repository.UserRepository;
import com.electrobuy.backend.repository.VendorApplicationRepository;
import com.electrobuy.backend.service.VendorApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/vendor-applications")
@CrossOrigin(origins = "*") // Let frontend application panels interact without security CORS flags blocking
                            // calls
@RequiredArgsConstructor // Automatically generates high-performance immutable constructor injection
public class VendorApplicationController {

    private final VendorApplicationRepository vendorApplicationRepository;
    private final UserRepository userRepository;
    private final VendorApplicationService vendorApplicationService;

    /**
     * Submit a new Vendor Application with a PDF Document
     * Consumes: MULTIPART_FORM_DATA_VALUE
     */
    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> applyForVendor(
            @RequestParam("userId") long userId,
            @RequestParam("businessName") String businessName,
            @RequestParam("gstNumber") String gstNumber,
            @RequestParam("pdfDocument") MultipartFile file) {

        try {
            // 1. Verify User exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User account not found"));

            // 2. File verification block (Flipped to be 100% null-safe)
            if (file.isEmpty() || !"application/pdf".equals(file.getContentType())) {
                return ResponseEntity.badRequest().body("Please upload a valid PDF document.");
            }

            // Example returned reference link after mock storage upload
            String uploadedDocumentUrl = "https://electrobuy-storage.s3.amazonaws.com/vendor-docs/"
                    + System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // 3. Build and map your application row entity
            VendorApplication application = new VendorApplication();
            application.setUser(user);
            application.setBusinessName(businessName);
            application.setGstNumber(gstNumber);
            application.setDocumentUrl(uploadedDocumentUrl);
            application.setStatus(VendorApplication.ApplicationStatus.PENDING);

            VendorApplication savedApplication = vendorApplicationRepository.save(application);
            return new ResponseEntity<>(savedApplication, HttpStatus.CREATED);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing application: " + e.getMessage());
        }
    }

    /**
     * Admin Endpoint: List applications filtered by approval state
     */
    @GetMapping("/admin/list")
    public ResponseEntity<List<VendorApplication>> getApplicationsByStatus(
            @RequestParam(value = "status", defaultValue = "PENDING") VendorApplication.ApplicationStatus status) {
        return ResponseEntity.ok(vendorApplicationRepository.findByStatus(status));
    }

    /**
     * Admin Endpoint: Approve or Reject a vendor registration request
     */
    @PutMapping("/admin/{id}/review")
    public ResponseEntity<?> reviewApplication(
            @PathVariable long id,
            @RequestParam("status") VendorApplication.ApplicationStatus newStatus) {
        try {
            // Check if application exists before invoking process pipelines
            if (!vendorApplicationRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            // ✅ CRITICAL BUG FIXED: Routed through VendorApplicationService to correctly
            // invoke
            // the full automation pipeline (role upgrading + merchant entity
            // auto-provisioning)
            vendorApplicationService.processApplicationApproval(id, newStatus.name());

            return ResponseEntity.ok("Application processed successfully. Status marked as: " + newStatus);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}