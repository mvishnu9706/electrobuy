package com.electrobuy.backend.dto;

import lombok.Data;

@Data
public class VendorRegistrationDto {
    // Basic Login account details
    private String fullName;
    private String email;
    private String password;

    // Commercial profile data
    private String businessName;
    private String gstNumber;
}