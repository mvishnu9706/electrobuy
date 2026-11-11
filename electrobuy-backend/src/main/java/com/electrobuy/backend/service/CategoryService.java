package com.electrobuy.backend.service;

import com.electrobuy.backend.dto.CategoryRequest;
import com.electrobuy.backend.entity.Category;
import com.electrobuy.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Constructor Injection (Best practice)
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // --- METHOD TO CREATE A CATEGORY ---
    public Category createCategory(CategoryRequest request) {
        Category newCategory = new Category();
        newCategory.setName(request.getName());

        // Check if the user passed a parent_id
        Long parentId = request.getParentId();
        if (parentId != null) {
            // Find the parent in the database
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(
                            () -> new RuntimeException("Parent Category not found with ID: " + parentId));

            // Link the new category to its parent
            newCategory.setParent(parent);
        }

        // Save to the database
        return categoryRepository.save(newCategory);
    }

    // --- METHOD TO FETCH THE CATEGORY TREE ---
    public List<Category> getAllCategories() {
        // We only fetch the Main Categories.
        // Because of our Entity setup, fetching main categories will automatically
        // fetch their subcategories!
        return categoryRepository.findByParentIsNull();
    }
}