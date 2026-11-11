package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.VendorApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VendorApplicationRepository extends JpaRepository<VendorApplication, Long> {

    // Used to look up a specific user's submitted request
    Optional<VendorApplication> findByUserId(Long userId);

    // Used by admins to filter incoming pending registration requests
    List<VendorApplication> findByStatus(VendorApplication.ApplicationStatus status);
}