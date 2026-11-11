package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.Inventory;
import com.electrobuy.backend.entity.Product;
import com.electrobuy.backend.entity.ProductVariant;
import com.electrobuy.backend.repository.ProductRepository;
import com.electrobuy.backend.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private final ProductVariantRepository variantRepository;
    private final ProductRepository productRepository;

    /**
     * Provision and bind a new specific combination variant line onto an existing
     * parent product page.
     */
    @Transactional
    public ProductVariant addVariantToProduct(long productId, ProductVariant variant, int initialStock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Parent product page mapping not found for ID: " + productId));

        // ⚙️ AUTOMATIC GENERATION: Enforce unique e-commerce SKU formatting if none was
        // passed
        if (variant.getSku() == null || variant.getSku().trim().isEmpty()) {
            variant.setSku(generateAutomatedSku(product, variant));
        } else {
            variant.setSku(variant.getSku().toUpperCase().trim().replaceAll("\\s+", "-"));
        }

        // Auto-configure tracking inventory metrics per variant line
        Inventory inventory = new Inventory();
        inventory.setStockQuantity(initialStock);
        variant.setInventory(inventory);

        variant.setProduct(product);

        // Turn on the variable flag on parent product container
        if (!product.isVariable()) {
            product.setVariable(true);
            productRepository.save(Objects.requireNonNull(product));
        }

        return variantRepository.save(Objects.requireNonNull(variant));
    }

    public List<ProductVariant> getVariantsByProductId(long productId) {
        return variantRepository.findByProduct_Id(productId);
    }

    public ProductVariant getVariantBySku(String sku) {
        return variantRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Variant barcode matching SKU not found: " + sku));
    }

    @Transactional
    public ProductVariant updateVariantDetails(long variantId, ProductVariant fields) {
        ProductVariant existing = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Product variant record not found for ID: " + variantId));

        existing.setPrice(fields.getPrice());
        existing.setColor(fields.getColor());
        existing.setStorage(fields.getStorage());
        existing.setSize(fields.getSize());

        if (fields.getSku() != null && !fields.getSku().trim().isEmpty()) {
            existing.setSku(fields.getSku().toUpperCase().trim().replaceAll("\\s+", "-"));
        }

        return variantRepository.save(Objects.requireNonNull(existing));
    }

    @Transactional
    public void removeVariant(long variantId) {
        if (!variantRepository.existsById(variantId)) {
            throw new RuntimeException("Cannot delete: Variant identifier not found.");
        }
        variantRepository.deleteById(variantId);
    }

    // --- Private Helper Automation Formula Utility ---
    private String generateAutomatedSku(Product parent, ProductVariant variant) {
        StringBuilder skuBuilder = new StringBuilder();
        skuBuilder.append(parent.getBrand().substring(0, Math.min(parent.getBrand().length(), 3)).toUpperCase());
        skuBuilder.append("-");
        skuBuilder.append(parent.getName().substring(0, Math.min(parent.getName().length(), 4)).toUpperCase());

        if (variant.getColor() != null && !variant.getColor().isEmpty()) {
            skuBuilder.append("-")
                    .append(variant.getColor().substring(0, Math.min(variant.getColor().length(), 3)).toUpperCase());
        }
        if (variant.getStorage() != null && !variant.getStorage().isEmpty()) {
            skuBuilder.append("-").append(variant.getStorage().toUpperCase());
        }

        return skuBuilder.toString().replaceAll("[^A-Z0-9-]", "");
    }
}