package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    // Find a specific variant option by its warehouse barcode string
    Optional<ProductVariant> findBySku(String sku);

    // Fetch all stock variants tied to a parent container product page
    List<ProductVariant> findByProduct_Id(long productId);
}