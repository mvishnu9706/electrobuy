package com.electrobuy.backend.controller;

import com.electrobuy.backend.entity.ProductVariant;
import com.electrobuy.backend.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-variants")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService variantService;

    /**
     * Create and attach a new selectable specification option model variant under a
     * parent product.
     * URL: POST
     * http://localhost:8085/api/product-variants/add/product/{productId}?initialStock=30
     */
    @PostMapping("/add/product/{productId}")
    public ResponseEntity<ProductVariant> createVariant(
            @PathVariable long productId,
            @RequestBody ProductVariant variant,
            @RequestParam(defaultValue = "0") int initialStock) {
        ProductVariant savedVariant = variantService.addVariantToProduct(productId, variant, initialStock);
        return new ResponseEntity<>(savedVariant, HttpStatus.CREATED);
    }

    /**
     * Fetch all structural item options linked to an individual product root
     * framework.
     * URL: GET http://localhost:8085/api/product-variants/product/{productId}
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductVariant>> getVariantsByProduct(@PathVariable long productId) {
        return ResponseEntity.ok(variantService.getVariantsByProductId(productId));
    }

    /**
     * Fetch a specific singular variant combination layout card directly using its
     * structural SKU label.
     * URL: GET http://localhost:8085/api/product-variants/sku/{sku}
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<?> getVariantBySku(@PathVariable String sku) {
        try {
            return ResponseEntity.ok(variantService.getVariantBySku(sku));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Modify price lines or attributes details belonging to an isolated variation
     * profile row.
     * URL: PUT http://localhost:8085/api/product-variants/update/{variantId}
     */
    @PutMapping("/update/{variantId}")
    public ResponseEntity<ProductVariant> updateVariant(
            @PathVariable long variantId,
            @RequestBody ProductVariant variantFields) {
        return ResponseEntity.ok(variantService.updateVariantDetails(variantId, variantFields));
    }

    /**
     * Permanently drop an individual variation option row directly out of database
     * catalogs safely.
     * URL: DELETE http://localhost:8085/api/product-variants/{variantId}
     */
    @DeleteMapping("/{variantId}")
    public ResponseEntity<String> deleteVariant(@PathVariable long variantId) {
        try {
            variantService.removeVariant(variantId);
            return ResponseEntity.ok("Product modification variant option was successfully eliminated.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}