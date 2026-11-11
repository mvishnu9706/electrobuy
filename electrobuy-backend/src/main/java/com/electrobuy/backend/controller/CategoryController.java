package com.electrobuy.backend.controller;

import com.electrobuy.backend.entity.Category;
import com.electrobuy.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor // Automatically generates high-performance immutable constructor injection
public class CategoryController {

    // Field marked final to eliminate IDE linting warnings and achieve absolute
    // null safety
    private final CategoryRepository categoryRepository;

    /**
     * 1. GET ALL
     * Fetch all categories registered in the marketplace.
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    /**
     * 2. POST (Create Mapping)
     * If the JSON includes a parent with an ID, Hibernate handles the link
     * automatically.
     */
    @PostMapping
    @SuppressWarnings("null")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category savedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(savedCategory);
    }

    /**
     * 3. GET MAIN CATEGORIES
     * Retrieves root level categories that don't belong to a parent category.
     */
    @GetMapping("/main")
    public ResponseEntity<List<Category>> getMainCategories() {
        return ResponseEntity.ok(categoryRepository.findByParentIsNull());
    }

    /**
     * 4. PUT (Update Mapping)
     * Updates an existing category's properties safely.
     */
    @PutMapping("/{id}")

    public ResponseEntity<Category> updateCategory(@PathVariable long id, @RequestBody Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Update name
        category.setName(categoryDetails.getName());

        // Update parent logic
        // If categoryDetails.getParent() is null, it removes the parent relationship.
        // If it has data, it establishes it.
        category.setParent(categoryDetails.getParent());

        Category updatedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * 5. DELETE
     * Drops a category record cleanly from the catalog mappings via its primitive
     * identifier.
     */
    @DeleteMapping("/{id}")

    public ResponseEntity<String> deleteCategory(@PathVariable long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.ok("Category deleted successfully");
    }
}