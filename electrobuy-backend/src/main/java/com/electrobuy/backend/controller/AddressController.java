package com.electrobuy.backend.controller;

import com.electrobuy.backend.entity.Address;
import com.electrobuy.backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor // Automatically generates high-performance immutable constructor injection
public class AddressController {

    // Field marked final to eliminate IDE linting warnings and achieve absolute
    // null safety
    private final AddressService addressService;

    /**
     * Endpoint to add an address profile for a customer.
     */
    @PostMapping("/add/{userId}")
    public Address addAddress(@PathVariable long userId, @RequestBody Address address) {
        return addressService.addAddress(userId, address);
    }

    /**
     * Endpoint to retrieve all addresses belonging to a specific customer
     * identifier.
     */
    @GetMapping("/user/{userId}")
    public List<Address> getAddresses(@PathVariable long userId) {
        return addressService.getUserAddresses(userId);
    }

    /**
     * Endpoint to drop an address record out of database mapping structures safely.
     */
    @DeleteMapping("/{addressId}")
    public String deleteAddress(@PathVariable long addressId) {
        addressService.deleteAddress(addressId);
        return "Address deleted successfully";
    }
}