package com.electrobuy.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * ProductVariant Entity.
 * Maps attributes to specialized columns as seen in image_79eeab.png.
 */
@Entity
@Table(name = "product_variants")
@Data
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties("variants")
    private Product product;

    @Column(unique = true, nullable = false, length = 255)
    private String sku; // Unique Stock Keeping Unit barcode tracker

    @Column(nullable = false)
    private double price;

    @Column(name = "variant_color", length = 255)
    private String color; // e.g., "Space Gray", "Midnight Black"

    @Column(name = "variant_storage", length = 255)
    private String storage; // e.g., "128GB", "1TB"

    @Column(name = "variant_size", length = 255)
    private String size; // e.g., "Standard", "Pro"

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;
}