package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.Address;
import com.electrobuy.backend.entity.User;
import com.electrobuy.backend.repository.AddressRepository;
import com.electrobuy.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Automatically generates constructor injection for all final fields
public class AddressService {

    // Marked as 'final' to allow Lombok to wire them into the constructor loop
    // cleanly
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    /**
     * Associates a shipping/billing address record to a specific user profile.
     * Automatically sets the address as default if it's the user's first entry.
     */
    @Transactional
    public Address addAddress(long userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If this is the user's first address, make it default automatically
        List<Address> existing = addressRepository.findByUserId(userId);
        if (existing.isEmpty()) {
            address.setDefault(true);
        }

        address.setUser(user);
        return addressRepository.save(address);
    }

    /**
     * Retrieves all saved address locations matching a specific user ID.
     */
    public List<Address> getUserAddresses(long userId) {
        return addressRepository.findByUserId(userId);
    }

    /**
     * Drops an address row out of storage grids cleanly.
     * Added safety existence check to prevent silent JPA execution crashes.
     */
    @Transactional
    public void deleteAddress(long addressId) {
        if (!addressRepository.existsById(addressId)) {
            throw new RuntimeException("Target address record not found with ID: " + addressId);
        }
        addressRepository.deleteById(addressId);
    }
}