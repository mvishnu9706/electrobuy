package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 🔍 Target lookup rule for fetching via SEO-optimized clean URLs
    Optional<Product> findBySeoSlug(String seoSlug);

    // 🗂️ Finds all products inside a specific category.
    // TWEAK: Removed the underscore and changed 'long' to 'Long' to match standard
    // entity types.
    List<Product> findByCategoryId(Long categoryId);

    // 💰 Price filtering for your frontend price sliders!
    // TWEAK: Changed 'double' to 'Double' to perfectly match the field we just
    // added to Product.java.
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // 🏢 Brand filtering
    List<Product> findByBrand(String brand);
}