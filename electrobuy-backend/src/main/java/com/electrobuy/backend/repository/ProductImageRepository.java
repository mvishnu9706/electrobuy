package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    // Finds all alternative media assets belonging to a target product record
    List<ProductImage> findByProductId(Long productId);
}