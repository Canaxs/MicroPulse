package com.micro.product_service.unit;

import com.micro.product_service.dto.CategoryDTO;
import com.micro.product_service.persistence.entity.Category;
import com.micro.product_service.persistence.repository.CategoryRepository;
import com.micro.product_service.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceUnitTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepository);
    }

    @Test
    void create_shouldSaveCategory() {
        Long parentId = 10L;

        CategoryDTO categoryDTO = CategoryDTO.builder()
                .name("Ayakkabı")
                .parentId(parentId)
                .build();

        Category parentCategory = new Category(parentId, "Giyim", null, null);

        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory));

        Category savedCategory = new Category(2L, "Ayakkabı", parentCategory, null);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryDTO result = categoryService.createCategory(categoryDTO);

        assertNotNull(result);
        assertEquals("Ayakkabı", result.getName());

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());

        Category saved = captor.getValue();
        assertEquals("Ayakkabı", saved.getName());
        assertEquals(parentCategory, saved.getParentCategory());

        verify(categoryRepository).findById(parentId);
        verify(categoryRepository).save(any(Category.class));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void getCategoryById_shouldReturnCategoryDTO_whenCategoryExists() {
        Long id = 1L;
        Optional<Category> getCategory = Optional.of(new Category(id, "Erkek", null, null));

        when(categoryRepository.findById(anyLong())).thenReturn(getCategory);

        CategoryDTO categoryDTO = categoryService.getCategoryById(id);

        assertNotNull(categoryDTO);
        assertEquals(getCategory.get().getName(), categoryDTO.getName());
        assertEquals(getCategory.get().getId(), categoryDTO.getId());

        verify(categoryRepository).findById(id);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void update_shouldUpdateCategory() {
        Long categoryId = 3L;
        Long parentId = 1L;

        CategoryDTO categoryDTO = CategoryDTO.builder()
                .id(categoryId)
                .name("Ayakkabı")
                .parentId(parentId)
                .build();

        Category parentCategory = new Category(parentId, "Erkek", null, null);
        Category existingCategory = new Category(categoryId, "Eski Ayakkabı", null, null);
        Category updatedCategory = new Category(categoryId, "Ayakkabı", parentCategory, null);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        CategoryDTO result = categoryService.updateCategory(categoryDTO);

        assertNotNull(result);
        assertEquals("Ayakkabı", result.getName());
        assertEquals(categoryId, result.getId());
        assertEquals(parentId, result.getParentId());

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        Category captured = captor.getValue();

        assertEquals("Ayakkabı", captured.getName());
        assertEquals(parentCategory, captured.getParentCategory());

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findById(parentId);
        verify(categoryRepository).save(any(Category.class));
        verifyNoMoreInteractions(categoryRepository);

    }

    @Test
    void delete_shouldDeleteCategory_whenIdIsValid() {
        Long categoryId = 1L;
        Category category = new Category(categoryId, "Erkek", null, null);

        when(categoryRepository.getReferenceById(anyLong())).thenReturn(category);

        doNothing().when(categoryRepository).delete(category);

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository).getReferenceById(categoryId);
        verify(categoryRepository).delete(category);
        verifyNoMoreInteractions(categoryRepository);

    }

    @Test
    void getCategory_shouldGetCategoryTree_whenCategoryExists() {
        Category childCategory = new Category();
        childCategory.setId(2L);
        childCategory.setName("Tişört");

        Category parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setName("Giyim");
        parentCategory.setSubCategories(List.of(childCategory));

        childCategory.setParentCategory(parentCategory);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));

        CategoryDTO result = categoryService.getCategoryTree(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Giyim", result.getName());
        assertNull(result.getParentId());

        assertNotNull(result.getSubCategories());
        assertEquals(1, result.getSubCategories().size());

        CategoryDTO childDTO = result.getSubCategories().get(0);
        assertEquals(2L, childDTO.getId());
        assertEquals("Tişört", childDTO.getName());
        assertEquals(1L, childDTO.getParentId());

        verify(categoryRepository).findById(1L);
    }

    @Test
    void getAllRootCategories_shouldReturnListOfRootCategoryDTOs() {
        CategoryDTO root1 = new CategoryDTO(1L, "Erkek", null, null);
        CategoryDTO root2 = new CategoryDTO(2L, "Kadın", null, null);

        List<CategoryDTO> rootCategories = List.of(root1, root2);

        when(categoryRepository.findByParentCategoryIsNull()).thenReturn(rootCategories);

        List<CategoryDTO> result = categoryService.getAllRootCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Erkek", result.get(0).getName());
        assertEquals("Kadın", result.get(1).getName());

        verify(categoryRepository).findByParentCategoryIsNull();
        verifyNoMoreInteractions(categoryRepository);
    }

}
