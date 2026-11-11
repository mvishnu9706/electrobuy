package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 📥 Finds all items inside a specific customer's cart
    List<CartItem> findByUserId(Long userId);

    // 🔍 Checks if a customer already added a specific variant to their cart
    Optional<CartItem> findByUserIdAndProductVariantId(Long userId, Long variantId);

    // 🗑️ Empties the cart completely right after a successful checkout purchase
    void deleteByUserId(Long userId);
}