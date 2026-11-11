package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Locates inventory allocations linked directly to a product ID
    Optional<Inventory> findByProductId(long productId);
}