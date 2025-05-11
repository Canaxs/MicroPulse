package com.micro.product_service.service.impl;

import com.micro.product_service.dto.CategoryDTO;
import com.micro.product_service.persistence.entity.Category;
import com.micro.product_service.persistence.repository.CategoryRepository;
import com.micro.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;

    @Override
    @CachePut(cacheNames = "categories", key = "#result.id")
    @CacheEvict(cacheNames = {"category_tree", "root_categories"}, allEntries = true)
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
    @Cacheable(cacheNames = "categories", key = "#id")
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapToDTO(category);
    }

    @Override
    @CachePut(cacheNames = "categories", key = "#categoryDTO.id")
    @CacheEvict(cacheNames = {"category_tree", "root_categories"}, allEntries = true)
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
    @Caching(evict = {
            @CacheEvict(cacheNames = "categories", key = "#id"),
            @CacheEvict(cacheNames = {"category_tree", "root_categories"}, allEntries = true)
    })
    public void deleteCategory(Long id) {
        try {
            categoryRepository.delete(categoryRepository.getReferenceById(id));
        }
        catch (Exception e) {
            throw new RuntimeException("Delete failed");
        }
    }

    @Override
    @Cacheable(cacheNames = "category_tree", key = "#parentId")
    public CategoryDTO getCategoryTree(Long parentId) {
        Category category = categoryRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return buildCategoryTree(category);
    }

    @Override
    @Cacheable(cacheNames = "root_categories")
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
