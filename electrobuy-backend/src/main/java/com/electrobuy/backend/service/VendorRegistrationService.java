package com.electrobuy.backend.service;

import com.electrobuy.backend.dto.VendorRegistrationDto;
import com.electrobuy.backend.entity.User;
import com.electrobuy.backend.entity.Role;
import com.electrobuy.backend.entity.VendorApplication;
import com.electrobuy.backend.repository.UserRepository;
import com.electrobuy.backend.repository.VendorApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor // Automatically handles high-performance immutable constructor injection
public class VendorRegistrationService {

    // Fields marked final to eliminate IDE linting warnings and achieve absolute
    // null type-safety
    private final UserRepository userRepository;
    private final VendorApplicationRepository vendorApplicationRepository;

    // Initialized cleanly inline and kept immutable alongside constructor
    // configurations
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Local folder to store uploaded verification PDFs
    private final String uploadDir = "uploads/vendor_docs/";

    /**
     * Handles the onboarding registration flow for a new merchant.
     * Provisions a baseline User profile with VENDOR_PENDING rights, safely writes
     * proof documentation to disk, and tracks the application for administrative
     * review.
     */
    @Transactional

    public void registerNewVendor(VendorRegistrationDto dto, MultipartFile pdfFile) throws IOException {

        // 🛡️ Safety Check: Intercept duplicate profiles before hitting database
        // constraint walls
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Registration failed: An account with this email address already exists.");
        }

        // 1. Create central login user account with restricted VENDOR_PENDING
        // permissions
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());

        // ✅ Password Encryption Integrated smoothly from your security guidelines
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.VENDOR_PENDING);
        user.setActive(true); // User is active but restricted by their pending role flag
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // 2. Handle physical document saving mechanics safely
        String uniqueFileName = UUID.randomUUID().toString() + "_" + pdfFile.getOriginalFilename();
        Path copyLocation = Paths.get(uploadDir + uniqueFileName);

        Files.createDirectories(copyLocation.getParent());
        Files.copy(pdfFile.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

        // 3. Create the Vendor Onboarding Application Tracker record
        VendorApplication application = new VendorApplication();
        application.setUser(savedUser);
        application.setBusinessName(dto.getBusinessName());
        application.setGstNumber(dto.getGstNumber());
        application.setDocumentUrl(copyLocation.toString());
        application.setStatus(VendorApplication.ApplicationStatus.PENDING);

        vendorApplicationRepository.save(application);
    }
}