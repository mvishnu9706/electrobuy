package com.electrobuy.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(length = 2000)
    private String description;

    // 🚀 INJECTED FIX: The missing price field your Repository was looking for!
    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ACTIVE;

    private LocalDateTime createdAt = LocalDateTime.now();

    // ==========================================
    // 🚀 ENTERPRISE SEO FIELDS
    // ==========================================

    @Column(name = "seo_slug", unique = true, nullable = false)
    private String seoSlug; // e.g., "iphone-15-pro-max"

    @Column(name = "meta_title", length = 70)
    private String metaTitle;

    @Column(name = "meta_description", length = 160)
    private String metaDescription;

    @Column(name = "seo_keywords")
    private String seoKeywords;

    // ==========================================
    // 🛠️ VARIABLE PRODUCT ENGINE MAPPINGS
    // ==========================================

    @Column(name = "is_variable", nullable = false)
    private boolean isVariable = false;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnoreProperties("product")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProductVariant> variants = new ArrayList<>();

    // ==========================================
    // CORE SYSTEM RELATIONS
    // ==========================================

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnoreProperties("product")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProductImage> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    @JsonIgnoreProperties("products")
    private Vendor store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnoreProperties("products")
    private Category category;

    // ==========================================
    // 🧠 AUTOMATED DYNAMIC SEO FALLBACKS
    // ==========================================

    /**
     * Captures or dynamically builds a search-friendly title utilizing product,
     * brand, and main category definitions.
     */
    public String getEffectiveMetaTitle() {
        if (this.metaTitle != null && !this.metaTitle.trim().isEmpty()) {
            return this.metaTitle;
        }

        // Dynamically injects the parent category grouping if the relational mapping
        // exists
        String categorySegment = (this.category != null) ? " " + this.category.getName() : "";
        return this.name + " by " + this.brand + " - Buy" + categorySegment + " Online | ElectroBuy";
    }

    /**
     * Synthesizes a high-conversion marketing meta summary using the main category
     * context path tags.
     */
    public String getEffectiveMetaDescription() {
        if (this.metaDescription != null && !this.metaDescription.trim().isEmpty()) {
            return this.metaDescription;
        }

        // Fallback formula prioritizing category context parameters
        if (this.category != null) {
            return "Looking for the best price on " + this.name + " by " + this.brand
                    + "? Browse our curated selection of premium "
                    + this.category.getName().toLowerCase() + " at ElectroBuy with safe home delivery!";
        }

        // Standard structural character truncation fallback
        if (this.description != null && this.description.length() > 150) {
            return this.description.substring(0, 147) + "...";
        }
        return this.description;
    }
}