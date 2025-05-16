package com.micro.product_service.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.product_service.dto.CategoryDTO;
import com.micro.product_service.persistence.entity.Category;
import com.micro.product_service.persistence.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        categoryRepository.deleteAll();
    }

    @Test
    void createCategory_shouldReturnCreatedCategory() throws Exception {
        CategoryDTO dto = CategoryDTO.builder()
                .name("Bilgisayar")
                .build();

        mockMvc.perform(post("/category/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Bilgisayar"));
    }

    @Test
    void getCategoryById_shouldReturnCategory() throws Exception {
        Category saved = categoryRepository.save(new Category(null, "Telefon", null, null));

        mockMvc.perform(get("/category/category-id/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Telefon"));
    }

    @Test
    void updateCategory_shouldReturnUpdatedCategory() throws Exception {
        Category parent = categoryRepository.save(new Category(null, "Elektronik", null, null));
        Category child = categoryRepository.save(new Category(null, "Tablet", null, null));

        CategoryDTO dto = CategoryDTO.builder()
                .id(child.getId())
                .name("iPad")
                .parentId(parent.getId())
                .build();

        mockMvc.perform(put("/category/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("iPad"))
                .andExpect(jsonPath("$.parentId").value(parent.getId()));
    }

    @Test
    void deleteCategory_shouldRemoveCategory() throws Exception {
        Category saved = categoryRepository.save(new Category(null, "Silinecek", null, null));

        mockMvc.perform(delete("/category/delete/" + saved.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/category/category-id/" + saved.getId()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getCategoryTree_shouldReturnCategoryWithSubCategories() throws Exception {
        Category parent = new Category(null, "Giyim", null, null);
        parent = categoryRepository.save(parent);

        Category sub = new Category(null, "Pantolon", parent, null);
        categoryRepository.save(sub);

        mockMvc.perform(get("/category/category-tree/" + parent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Giyim"))
                .andExpect(jsonPath("$.subCategories", hasSize(1)))
                .andExpect(jsonPath("$.subCategories[0].name").value("Pantolon"));
    }

    @Test
    void getAllRootCategories_shouldReturnOnlyRootOnes() throws Exception {
        Category root1 = new Category(null, "Anne", null, null);
        Category root2 = new Category(null, "Baba", null, null);
        categoryRepository.saveAll(List.of(root1, root2));

        mockMvc.perform(get("/category/root-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Anne", "Baba")));
    }
}
