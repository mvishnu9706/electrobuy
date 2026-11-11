package com.electrobuy.backend.dto;

public class CategoryRequest {

    private String name;

    // If this is null, it's a Main Category. If it has a number, it's a
    // Subcategory.
    private Long parentId;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}