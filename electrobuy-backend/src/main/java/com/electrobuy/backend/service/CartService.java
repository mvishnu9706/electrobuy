package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.CartItem;
import com.electrobuy.backend.entity.ProductVariant;
import com.electrobuy.backend.entity.User;
import com.electrobuy.backend.repository.CartRepository;
import com.electrobuy.backend.repository.ProductVariantRepository;
import com.electrobuy.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor // Automatically generates high-performance immutable constructor injection
public class CartService {

    private final CartRepository cartRepository;

    // 🚀 FIX: Swapped ProductRepository for ProductVariantRepository
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    /**
     * Add a product variant item to a user's shopping cart with robust inventory
     * verification checks.
     */
    @Transactional
    public CartItem addItemToCart(long userId, long variantId, int quantity) {

        // 1. Fetch the exact variant the user wants
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Product Variant not found with ID: " + variantId));

        // 2. Extract stock directly from the variant
        int availableStock = 0;
        if (variant.getInventory() != null) {
            availableStock = variant.getInventory().getStockQuantity();
        }

        // Safety verification block checking root availability boundaries
        if (availableStock < quantity) {
            throw new RuntimeException("Insufficient vendor stock level available.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User registration record not found with ID: " + userId));

        // Locate existing item record rows within the database cart mappings to
        // calculate step increments
        CartItem existingItem = cartRepository.findByUserAndProductVariant(user, variant).orElse(null);

        if (existingItem != null) {
            int totalRequestedQuantity = existingItem.getQuantity() + quantity;
            if (availableStock < totalRequestedQuantity) {
                throw new RuntimeException("Cannot increment selection: Exceeds available warehouse stock metrics.");
            }
            existingItem.setQuantity(totalRequestedQuantity);
            return cartRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProductVariant(variant);
            newItem.setQuantity(quantity);

            // 🚀 FIX: Lock in the price at the exact moment of addition
            newItem.setPriceAtAddition(variant.getPrice());

            return cartRepository.save(newItem);
        }
    }

    /**
     * Retrieve the complete active shopping basket for a specific client profile.
     */
    public List<CartItem> getCartItems(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User profile registry entry not found for ID: " + userId));
        return cartRepository.findByUser(user);
    }

    /**
     * Modify target selection quantity counts for a single row mapped item index
     * line.
     */
    @Transactional
    public CartItem updateItemQuantity(long cartItemId, int newQuantity) {
        CartItem item = cartRepository.findById(cartItemId)
                .orElseThrow(
                        () -> new RuntimeException("Shopping cart entry line not found for identifier: " + cartItemId));

        if (newQuantity <= 0) {
            // Explicitly assert non-null condition to clear static analysis warnings
            cartRepository.delete(Objects.requireNonNull(item));
            return null;
        }

        // 🚀 FIX: Extract stock restrictions directly from the linked ProductVariant
        int availableStock = 0;
        ProductVariant variant = item.getProductVariant();

        if (variant != null && variant.getInventory() != null) {
            availableStock = variant.getInventory().getStockQuantity();
        }

        if (availableStock < newQuantity) {
            throw new RuntimeException(
                    "Operation rejected: Requested modification quantity exceeds active warehouse storage capacity.");
        }

        item.setQuantity(newQuantity);
        return cartRepository.save(item);
    }

    /**
     * Remove an individual entry row out of a user's active shopping allocation
     * panel.
     */
    @Transactional
    public void removeItemFromCart(long cartItemId) {
        if (!cartRepository.existsById(cartItemId)) {
            throw new RuntimeException("Cannot complete removal: Cart item row not located.");
        }
        cartRepository.deleteById(cartItemId);
    }

    /**
     * Completely wipe clear the entire persistent shopping array grid records
     * matching a targeted user.
     */
    @Transactional
    public void clearUserCart(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User account not found for ID: " + userId));

        List<CartItem> userItems = cartRepository.findByUser(user);
        if (!userItems.isEmpty()) {
            cartRepository.deleteAll(userItems);
        }
    }
}