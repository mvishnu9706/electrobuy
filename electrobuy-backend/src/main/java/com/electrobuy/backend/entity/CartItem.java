package com.electrobuy.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "cart_items")
@Data
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the User who owns this cart row
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({ "vendor", "addresses", "vendorApplication", "password", "createdAt", "updatedAt" })
    private User user;

    // 🚀 FIX 1: Swapped 'Product' for 'ProductVariant' to match the warehouse SKU
    // logic
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_variant_id", nullable = false)
    @JsonIgnoreProperties("product") // Prevents massive infinite JSON loops
    private ProductVariant productVariant;

    @Column(nullable = false)
    private int quantity;

    // 🚀 FIX 2: Added the historical price tracker needed by the CartService totals
    private Double priceAtAddition;
}