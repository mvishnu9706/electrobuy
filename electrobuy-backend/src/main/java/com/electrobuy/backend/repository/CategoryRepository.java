package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Fetches top-level categories (Electronics, Fashion, etc.) for the nav bar
    List<Category> findByParentIsNull();

    // Optional: Find subcategories of a specific parent
    List<Category> findByParentId(Long parentId);
}