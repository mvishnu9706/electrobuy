package com.electrobuy.backend.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class that initializes the official Razorpay client SDK.
 * Reads keys securely from application.properties.
 */
@Configuration
public class RazorpayConfig {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        // Initializes the Razorpay connection client with your credentials
        return new RazorpayClient(apiKey, apiSecret);
    }
}