package com.electrobuy.backend.controller;

import com.electrobuy.backend.entity.Inventory;
import com.electrobuy.backend.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor // Automatically generates high-performance immutable constructor injection
public class InventoryController {

    // Field marked final to eliminate IDE linting warnings and achieve absolute
    // null safety
    private final InventoryService inventoryService;

    /**
     * Endpoint to modify the active stock storage metric for an electronic product
     * listing.
     * URL: POST
     * http://localhost:8085/api/inventory/update?productId=1&stockQuantity=50
     */
    @PostMapping("/update")
    public ResponseEntity<Object> updateStockQuantity(@RequestParam long productId, @RequestParam int stockQuantity) {
        try {
            Inventory savedInventory = inventoryService.updateStock(productId, stockQuantity);
            return ResponseEntity.ok(savedInventory);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    /**
     * Endpoint to fetch warehouse tracking records containing specific product
     * availability parameters.
     * URL: GET http://localhost:8085/api/inventory/product/1
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<Object> getStockByProductId(@PathVariable long productId) {
        try {
            Inventory inventory = inventoryService.getStockByProductId(productId);
            return ResponseEntity.ok(inventory);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}