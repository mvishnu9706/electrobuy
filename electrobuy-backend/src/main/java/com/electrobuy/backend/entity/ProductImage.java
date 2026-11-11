package com.electrobuy.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "product_images")
@Data
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "image_url")
    private String imageUrl;

    // 🖥️ SYNCHRONIZED MATCH: Maps to your database schema's 'is_primary' boolean
    // column flag
    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;

    // 🖥️ SYNCHRONIZED MATCH: Maps to your database schema's explicit manual
    // 'alt_text' column field
    @Column(name = "alt_text", length = 500)
    private String altText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({ "images", "variants" })
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    // =======================================================
    // 🧠 FAILSAFE DYNAMIC IMAGE SEO ENGINE
    // =======================================================

    /**
     * Prioritizes your premium manual database alt_text description.
     * If the field is empty, it auto-synthesizes an optimized backup tag so SEO
     * never breaks.
     */
    public String getEffectiveAltText() {
        // 1. If manual explicit alt text is provided in the row, use it directly
        if (this.altText != null && !this.altText.trim().isEmpty()) {
            return this.altText.trim();
        }

        // 2. Fallback protection if the vendor left the field empty/null
        if (this.product == null) {
            return "ElectroBuy Product Image";
        }

        String categorySegment = (this.product.getCategory() != null)
                ? " " + this.product.getCategory().getName()
                : "";

        String viewType = this.isPrimary ? " Main Presentation" : " Detail View";

        return this.product.getName() + " by " + this.product.getBrand() + categorySegment + viewType + " | ElectroBuy";
    }

    /**
     * Generates a structural hover title asset tailored for e-commerce search
     * algorithms.
     */
    public String getEffectiveTitleText() {
        if (this.product == null) {
            return "View Product at ElectroBuy";
        }
        return "View detailed photo of " + this.product.getName();
    }
}