package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.CartItem;
import com.electrobuy.backend.entity.ProductVariant;
import com.electrobuy.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {

    // ==========================================
    // 🧠 OBJECT-BASED LOOKUPS
    // ==========================================

    /**
     * Fetch the complete collection of active shopping basket items assigned to a
     * single target user.
     * SQL equivalent: SELECT * FROM cart_items WHERE user_id = ?
     */
    List<CartItem> findByUser(User user);

    /**
     * 🚀 FIX APPLIED: Changed 'Product' to 'ProductVariant' to match your entity
     * update!
     * Core lookup routine used to verify if a unique user already has a specific
     * variant
     * added to their selection layout matrix.
     * SQL equivalent: SELECT * FROM cart_items WHERE user_id = ? AND
     * product_variant_id = ?
     */
    Optional<CartItem> findByUserAndProductVariant(User user, ProductVariant productVariant);

    // ==========================================
    // ⚡ ID-BASED LOOKUPS (Required by CartService)
    // ==========================================

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserIdAndProductVariantId(Long userId, Long variantId);

    void deleteByUserId(Long userId);
}