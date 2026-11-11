package com.electrobuy.backend.entity;

public enum Role {
    CUSTOMER,
    VENDOR_PENDING, // Added to track businesses awaiting admin document verification
    VENDOR,
    ADMIN
}