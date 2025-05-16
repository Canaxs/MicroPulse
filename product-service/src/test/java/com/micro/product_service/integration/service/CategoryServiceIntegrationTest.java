package com.micro.product_service.integration.service;

import com.micro.product_service.dto.CategoryDTO;
import com.micro.product_service.persistence.entity.Category;
import com.micro.product_service.persistence.repository.CategoryRepository;
import com.micro.product_service.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CategoryServiceIntegrationTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void createCategory_shouldSaveAndReturnCategoryDTO() {
        CategoryDTO dto = CategoryDTO.builder()
                .name("Elektronik")
                .build();

        CategoryDTO saved = categoryService.createCategory(dto);

        assertNotNull(saved.getId());
        assertEquals("Elektronik", saved.getName());

        Category found = categoryRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Elektronik", found.getName());
    }

    @Test
    void getCategoryById_shouldReturnSavedCategory() {
        Category saved = categoryRepository.save(new Category(null, "Moda", null, null));

        CategoryDTO result = categoryService.getCategoryById(saved.getId());

        assertEquals(saved.getId(), result.getId());
        assertEquals("Moda", result.getName());
    }

    @Test
    void updateCategory_shouldUpdateAndReturnCategoryDTO() {
        Category parent = categoryRepository.save(new Category(null, "Kadın", null, null));
        Category child = categoryRepository.save(new Category(null, "Elbise", null, null));

        CategoryDTO dto = CategoryDTO.builder()
                .id(child.getId())
                .name("Abiye")
                .parentId(parent.getId())
                .build();

        CategoryDTO updated = categoryService.updateCategory(dto);

        assertEquals("Abiye", updated.getName());
        assertEquals(parent.getId(), updated.getParentId());
    }

    @Test
    void deleteCategory_shouldRemoveCategory() {
        Category saved = categoryRepository.save(new Category(null, "Silinecek", null, null));

        categoryService.deleteCategory(saved.getId());

        assertFalse(categoryRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void getCategoryTree_shouldReturnTreeStructure() {
        Category parent = new Category(null, "Anne", null, null);
        parent = categoryRepository.save(parent);

        Category child = new Category(null, "Çocuk", parent, null);
        categoryRepository.save(child);

        CategoryDTO tree = categoryService.getCategoryTree(parent.getId());

        assertEquals("Anne", tree.getName());
        assertEquals(1, tree.getSubCategories().size());
        assertEquals("Çocuk", tree.getSubCategories().get(0).getName());
    }

    @Test
    void getAllRootCategories_shouldReturnOnlyRootOnes() {
        Category root1 = new Category(null, "Ev", null, null);
        Category root2 = new Category(null, "Bahçe", null, null);
        categoryRepository.saveAll(List.of(root1, root2));

        List<CategoryDTO> roots = categoryService.getAllRootCategories();

        assertEquals(2, roots.size());
        assertTrue(roots.stream().anyMatch(c -> c.getName().equals("Ev")));
        assertTrue(roots.stream().anyMatch(c -> c.getName().equals("Bahçe")));
    }
}
