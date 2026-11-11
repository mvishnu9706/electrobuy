package com.electrobuy.backend.dto;

// The inbound payload returned by the 3PL provider
public record ShippingResponse(
        String trackingNumber, // Also known as Air Waybill (AWB)
        String courierName,
        String labelUrl,
        String estimatedDeliveryDate) {
}