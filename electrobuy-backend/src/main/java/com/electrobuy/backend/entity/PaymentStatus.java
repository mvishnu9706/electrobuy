package com.electrobuy.backend.entity;

public enum PaymentStatus {
    PENDING, // User clicked checkout but hasn't paid yet
    COMPLETED, // Money successfully received
    FAILED, // Card declined or network error
    REFUNDED // Money returned to the customer
}