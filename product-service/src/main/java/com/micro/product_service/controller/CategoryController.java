package com.micro.product_service.controller;

import com.micro.product_service.dto.CategoryDTO;
import com.micro.product_service.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDto) {
        return ResponseEntity.ok(categoryService.createCategory(categoryDto));
    }

    @GetMapping("/category-id/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PutMapping("/update")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryDto));
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
    }

    @GetMapping("/category-tree/{parentId}")
    public ResponseEntity<CategoryDTO> getCategoryTree(@PathVariable("parentId") Long parentId) {
        return ResponseEntity.ok(categoryService.getCategoryTree(parentId));
    }

    @GetMapping("/root-categories")
    public ResponseEntity<List<CategoryDTO>> getAllRootCategories() {
        return ResponseEntity.ok(categoryService.getAllRootCategories());
    }

    @GetMapping("/category-depth/{level}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByDepth(@PathVariable("level") int level) {
        return ResponseEntity.ok(categoryService.getCategoriesByDepth(level));
    }
}
