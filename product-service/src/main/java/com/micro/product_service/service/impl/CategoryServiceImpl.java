package com.micro.product_service.service.impl;

import com.micro.product_service.dto.CategoryDTO;
import com.micro.product_service.persistence.entity.Category;
import com.micro.product_service.persistence.repository.CategoryRepository;
import com.micro.product_service.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());

        if (categoryDto.getParentId() != null) {
            Optional<Category> parentOpt = categoryRepository.findById(categoryDto.getParentId());
            parentOpt.ifPresent(category::setParentCategory);
        }

        Category saved = categoryRepository.save(category);
        return mapToDTO(saved);
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapToDTO(category);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDto) {
        Category category = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(categoryDto.getName());

        if (categoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }

        Category updated = categoryRepository.save(category);
        return mapToDTO(updated);
    }

    @Override
    public void deleteCategory(Long id) {
        try {
            categoryRepository.delete(categoryRepository.getReferenceById(id));
        }
        catch (Exception e) {
            throw new RuntimeException("Delete failed");
        }
    }

    @Override
    public CategoryDTO getCategoryTree(Long parentId) {
        Category category = categoryRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return buildCategoryTree(category);
    }

    @Override
    public List<CategoryDTO> getAllRootCategories() {
        return categoryRepository.findByParentCategoryIsNull();
    }

    @Override
    public List<CategoryDTO> getCategoriesByDepth(int level) {
        return null;
    }

    private CategoryDTO mapToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .subCategories(null)
                .build();
    }

    private CategoryDTO buildCategoryTree(Category category) {
        List<CategoryDTO> subCategoryDto = new ArrayList<>();
        if (category.getSubCategories() != null) {
            for (Category sub : category.getSubCategories()) {
                subCategoryDto.add(buildCategoryTree(sub));
            }
        }

        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .subCategories(subCategoryDto)
                .build();
    }

}
