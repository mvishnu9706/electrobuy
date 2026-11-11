package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.Product;
import com.electrobuy.backend.entity.ProductImage;
import com.electrobuy.backend.entity.ProductStatus;
import com.electrobuy.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 🚀 INTEGRATED AUDITING DIAL
    private final AuditLogService auditLogService;

    @Transactional
    public Product createProduct(long userId, long categoryId, Product product, int initialStock) {
        if (product.getSeoSlug() == null || product.getSeoSlug().trim().isEmpty()) {
            product.setSeoSlug(generateSlug(product.getName()));
        } else {
            product.setSeoSlug(generateSlug(product.getSeoSlug()));
        }

        product.setStatus(ProductStatus.ACTIVE);
        Product savedProduct = productRepository.save(Objects.requireNonNull(product));

        // 🚀 AUDIT EVENT
        auditLogService.logAction(
                "PRODUCT_CREATED",
                "Created product registration listing: " + savedProduct.getName() + " (SKU/ID: " + savedProduct.getId()
                        + ")");

        return savedProduct;
    }

    public Product getProductBySlug(String seoSlug) {
        return productRepository.findBySeoSlug(seoSlug)
                .orElseThrow(() -> new RuntimeException("Product profile not found for URL slug: " + seoSlug));
    }

    public List<Product> getAllActiveProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public Product addProductImages(long productId, List<ProductImage> images) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        for (ProductImage img : images) {
            img.setProduct(product);
            product.getImages().add(img);
        }

        Product savedProduct = productRepository.save(Objects.requireNonNull(product));

        // 🚀 AUDIT EVENT
        auditLogService.logAction(
                "PRODUCT_IMAGES_ADDED",
                "Appended " + images.size() + " new visual file paths to Product ID: " + productId);

        return savedProduct;
    }

    @Transactional
    public Product updateProductDetails(long productId, Product updatedFields) {
        Product existing = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String oldName = existing.getName();
        existing.setName(updatedFields.getName());
        existing.setBrand(updatedFields.getBrand());
        existing.setDescription(updatedFields.getDescription());
        existing.setSeoSlug(generateSlug(updatedFields.getName()));
        existing.setMetaTitle(updatedFields.getMetaTitle());
        existing.setMetaDescription(updatedFields.getMetaDescription());
        existing.setSeoKeywords(updatedFields.getSeoKeywords());

        Product savedProduct = productRepository.save(Objects.requireNonNull(existing));

        // 🚀 AUDIT EVENT
        auditLogService.logAction(
                "PRODUCT_UPDATED",
                "Modified details for Product ID: " + productId + ". Title shifted from '" + oldName + "' to '"
                        + savedProduct.getName() + "'");

        return savedProduct;
    }

    @Transactional
    public void softDeleteProduct(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStatus(ProductStatus.ARCHIVED);
        productRepository.save(Objects.requireNonNull(product));

        // 🚀 AUDIT EVENT
        auditLogService.logAction(
                "PRODUCT_SOFT_DELETED",
                "Archived and soft-deleted catalog entity tracking for Product ID: " + productId + " ("
                        + product.getName() + ")");
    }

    private String generateSlug(String text) {
        if (text == null)
            return "";
        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}