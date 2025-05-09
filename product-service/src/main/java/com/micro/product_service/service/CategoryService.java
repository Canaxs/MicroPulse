package com.micro.product_service.service;

import com.micro.product_service.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDto);

    CategoryDTO getCategoryById(Long id);

    CategoryDTO updateCategory(CategoryDTO categoryDto);

    void deleteCategory(Long id);

    CategoryDTO getCategoryTree(Long parentId);

    List<CategoryDTO> getAllRootCategories();

    List<CategoryDTO> getCategoriesByDepth(int level);

}
