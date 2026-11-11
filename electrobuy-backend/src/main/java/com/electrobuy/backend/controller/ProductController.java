package com.electrobuy.backend.controller;

import com.electrobuy.backend.entity.Product;
import com.electrobuy.backend.entity.ProductImage;
import com.electrobuy.backend.repository.ProductRepository;
import com.electrobuy.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    /**
     * Create a New Product Profile with automated SEO Slug extraction patterns.
     */
    @PostMapping("/vendor/{userId}/category/{categoryId}")
    public ResponseEntity<?> createProduct(
            @PathVariable long userId,
            @PathVariable long categoryId,
            @RequestBody Product product,
            @RequestParam int initialStock) {
        try {
            Product savedProduct = productService.createProduct(userId, categoryId, product, initialStock);
            return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * 🔍 ENTERPRISE LOOKUP LINK: Fetch single item card natively via its SEO URL
     * string.
     * URL: GET http://localhost:8085/api/products/detail/sony-wh-1000xm5-headphones
     */
    @GetMapping("/detail/{seoSlug}")
    public ResponseEntity<?> getProductBySlug(@PathVariable String seoSlug) {
        try {
            Product product = productService.getProductBySlug(seoSlug);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{productId}/images")
    public ResponseEntity<?> addImagesToProduct(@PathVariable long productId, @RequestBody List<ProductImage> images) {
        try {
            return ResponseEntity.ok(productService.addProductImages(productId, images));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{productId}/update")
    public ResponseEntity<?> updateProductDetails(@PathVariable long productId, @RequestBody Product fields) {
        try {
            return ResponseEntity.ok(productService.updateProductDetails(productId, fields));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable long productId) {
        try {
            productService.softDeleteProduct(productId);
            return ResponseEntity.ok("Marketplace product listing was successfully archived.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllActiveProducts());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        // 🚀 We removed the underscore to match the newly updated repository method!
        return ResponseEntity.ok(productRepository.findByCategoryId(categoryId));
    }

    @GetMapping("/filter/price")
    public ResponseEntity<List<Product>> getProductsByPrice(@RequestParam Double min, @RequestParam Double max) {
        return ResponseEntity.ok(productRepository.findByPriceBetween(min, max));
    }

    @GetMapping("/filter/brand")
    public ResponseEntity<List<Product>> getProductsByBrand(@RequestParam String brand) {
        return ResponseEntity.ok(productRepository.findByBrand(brand));
    }
}