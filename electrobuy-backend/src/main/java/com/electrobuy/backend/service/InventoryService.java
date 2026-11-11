package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.Inventory;
import com.electrobuy.backend.entity.Product;
import com.electrobuy.backend.repository.InventoryRepository;
import com.electrobuy.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor // Automatically generates the immutable constructor injection pattern behind
                         // the scenes
public class InventoryService {

    // Marked final to eliminate IDE linting warnings and achieve absolute null
    // type-safety
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    /**
     * Modifies the active stock storage metric for an electronic product listing.
     * Auto-provisions a tracking record if it doesn't exist yet.
     */
    @Transactional

    public Inventory updateStock(long productId, int stockQuantity) {
        if (stockQuantity < 0) {
            throw new RuntimeException("Inventory allocation cannot drop below a negative state.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product profile not found with ID: " + productId));

        // Find existing record, or instantiate a fresh inventory baseline
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseGet(() -> {
                    Inventory newInv = new Inventory();
                    newInv.setLowStockThreshold(5); // Establishes an enterprise standard safe threshold default
                    return newInv;
                });

        inventory.setProduct(product);
        inventory.setStockQuantity(stockQuantity);
        inventory.setLastUpdatedAt(LocalDateTime.now()); // Synchronizes modification auditing timestamps

        return inventoryRepository.save(inventory);
    }

    /**
     * Fetches the warehouse tracking record containing specific product
     * availability parameters.
     */
    public Inventory getStockByProductId(long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("No inventory profile listed for Product ID: " + productId));
    }
}