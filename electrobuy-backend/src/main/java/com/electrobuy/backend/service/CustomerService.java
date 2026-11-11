package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.CustomerProfile;
import com.electrobuy.backend.entity.User;
import com.electrobuy.backend.repository.CustomerProfileRepository;
import com.electrobuy.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // Automatically generates the immutable constructor injection loop
public class CustomerService {

    // Fields marked final to achieve true immutability and complete null
    // type-safety
    private final CustomerProfileRepository customerProfileRepository;
    private final UserRepository userRepository;

    /**
     * Retrieve active user profile data.
     * Auto-provisions a blank data row mapping if it doesn't exist yet.
     */
    @Transactional // Added transactional support here since it can write a new profile record
    public CustomerProfile getProfileByUserId(long userId) {
        return customerProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Auto-provision an empty profile table mapping if it doesn't exist yet
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Base User record not found"));
                    CustomerProfile newProfile = new CustomerProfile();
                    newProfile.setUser(user);
                    return customerProfileRepository.save(newProfile);
                });
    }

    /**
     * Update active user shipping parameters and default billing descriptors.
     */
    @Transactional
    public CustomerProfile updateShippingDetails(long userId, String address, String paymentMethod) {
        CustomerProfile profile = getProfileByUserId(userId);
        profile.setShippingAddress(address);
        profile.setDefaultPaymentMethod(paymentMethod);
        return customerProfileRepository.save(profile);
    }
}