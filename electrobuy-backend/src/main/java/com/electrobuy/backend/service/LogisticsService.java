package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.Shipment;
import com.electrobuy.backend.dto.ShippingRequest;
import com.electrobuy.backend.dto.ShippingResponse;

@Service
public interface LogisticsService {

    // Handles the outbound API call to the 3PL provider
    ShippingResponse initiateShipment(ShippingRequest request);

    // Orchestrates fetching the order, calling initiateShipment, and updating the
    // DB
    Shipment processAutomatedFulfillment(long orderId);
}