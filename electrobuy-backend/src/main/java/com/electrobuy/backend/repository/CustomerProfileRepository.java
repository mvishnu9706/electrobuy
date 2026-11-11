package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {
    // Custom query to look up profile preferences using the User Login ID
    Optional<CustomerProfile> findByUserId(Long userId);
}