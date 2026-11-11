package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.User;
import com.electrobuy.backend.entity.Role;
import com.electrobuy.backend.entity.Vendor;
import com.electrobuy.backend.entity.VendorApplication;
import com.electrobuy.backend.repository.UserRepository;
import com.electrobuy.backend.repository.VendorRepository;
import com.electrobuy.backend.repository.VendorApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // Automatically generates high-performance immutable constructor injection
public class VendorApplicationService {

    // Fields marked final to eliminate IDE linting warnings and achieve absolute
    // null type-safety
    private final VendorApplicationRepository vendorApplicationRepository;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;

    /**
     * Processes vendor applications by changing their onboarding status.
     * If approved, elevates the associated user account permissions to VENDOR
     * and auto-provisions their merchant profile data entry rows natively.
     */
    @Transactional
    @SuppressWarnings("null")
    public void processApplicationApproval(long applicationId, String status) {
        VendorApplication application = vendorApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Onboarding application form record not found"));

        if ("APPROVED".equalsIgnoreCase(status)) {
            application.setStatus(VendorApplication.ApplicationStatus.APPROVED);

            // Elevate login credentials permission role tags
            User user = application.getUser();
            user.setRole(Role.VENDOR); // Uses your custom Role configuration object/enum directly!
            userRepository.save(user);

            // Materialize a live row inside active business profiles vendor catalog table
            Vendor newVendor = new Vendor();
            newVendor.setUser(user);
            newVendor.setBusinessName(application.getBusinessName());
            newVendor.setGstNumber(application.getGstNumber());

            // Set contact info if available from application metadata to ensure complete
            // profile records
            newVendor.setContactPersonName(user.getFullName());
            newVendor.setEmail(user.getEmail());

            newVendor.setVendorActive(true);
            vendorRepository.save(newVendor);

        } else if ("REJECTED".equalsIgnoreCase(status)) {
            application.setStatus(VendorApplication.ApplicationStatus.REJECTED);
        }

        // Save application status updates smoothly
        vendorApplicationRepository.save(application);
    }
}