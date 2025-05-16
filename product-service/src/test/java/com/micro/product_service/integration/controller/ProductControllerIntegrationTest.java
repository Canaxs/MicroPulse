package com.micro.product_service.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.product_service.dto.ProductDTO;
import com.micro.product_service.persistence.entity.Product;
import com.micro.product_service.persistence.repository.ProductRepository;
import com.micro.product_service.persistence.repository.CategoryRepository;
import com.micro.product_service.persistence.entity.Category;
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
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        category = categoryRepository.save(new Category(null, "Elektronik", null, null));
    }

    @Test
    void createProduct_shouldReturnCreatedProduct() throws Exception {
        ProductDTO dto = ProductDTO.builder()
                .name("Laptop")
                .price(9999)
                .stock(10)
                .categoryId(category.getId())
                .build();

        mockMvc.perform(post("/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void getProductById_shouldReturnProduct() throws Exception {
        Product saved = productRepository.save(new Product(null, "Telefon", 5000, 5, category));

        mockMvc.perform(get("/product/by-product/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Telefon"));
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct() throws Exception {
        Product saved = productRepository.save(new Product(null, "Tablet", 3000, 20, category));

        ProductDTO dto = ProductDTO.builder()
                .id(saved.getId())
                .name("iPad")
                .price(3500)
                .stock(15)
                .categoryId(category.getId())
                .build();

        mockMvc.perform(put("/product/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("iPad"))
                .andExpect(jsonPath("$.stock").value(15));
    }

    @Test
    void deleteProduct_shouldRemoveProduct() throws Exception {
        Product saved = productRepository.save(new Product(null, "Silinecek Ürün", 1000, 2, category));

        mockMvc.perform(delete("/product/delete/" + saved.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/product/by-product/" + saved.getId()))
                .andExpect(status().is5xxServerError()); // Optional: 404 olursa değiştir.
    }

    @Test
    void getAllProducts_shouldReturnProductList() throws Exception {
        productRepository.saveAll(List.of(
                new Product(null, "Ürün 1", 100, 5, category),
                new Product(null, "Ürün 2", 200, 10, category)
        ));

        mockMvc.perform(get("/product/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getProductsByCategoryId_shouldReturnCategoryProducts() throws Exception {
        productRepository.saveAll(List.of(
                new Product(null, "Kategori Ürünü", 150, 3, category)
        ));

        mockMvc.perform(get("/product/by-category/" + category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Kategori Ürünü"));
    }

    @Test
    void searchProducts_shouldReturnPagedProducts() throws Exception {
        productRepository.save(new Product(null, "Aranacak Kelime", 800, 4, category));

        mockMvc.perform(get("/product/search")
                        .param("keyword", "Aranacak")
                        .param("size", "10")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Aranacak Kelime"));
    }

    @Test
    void getProductsByStockStatus_shouldReturnFiltered() throws Exception {
        productRepository.saveAll(List.of(
                new Product(null, "Stokta Var", 100, 5, category),
                new Product(null, "Stok Yok", 200, 0, category)
        ));

        mockMvc.perform(get("/product/stock-status")
                        .param("stock", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Stokta Var"));
    }

    @Test
    void getProductsByPriceRange_shouldReturnCorrectProducts() throws Exception {
        productRepository.saveAll(List.of(
                new Product(null, "Ucuz", 100, 5, category),
                new Product(null, "Pahalı", 10000, 5, category)
        ));

        mockMvc.perform(get("/product/price-range")
                        .param("min", "50")
                        .param("max", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ucuz"));
    }

    @Test
    void getRecentProducts_shouldReturnLimitedList() throws Exception {
        for (int i = 1; i <= 15; i++) {
            productRepository.save(new Product(null, "Ürün " + i, 100 + i, i, category));
        }

        mockMvc.perform(get("/product/recent")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    void updateStock_shouldChangeStockAmount() throws Exception {
        Product saved = productRepository.save(new Product(null, "Stok Güncelle", 200, 5, category));

        mockMvc.perform(put("/product/update-stock")
                        .param("productId", String.valueOf(saved.getId()))
                        .param("quantity", "20"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/product/by-product/" + saved.getId()))
                .andExpect(jsonPath("$.stock").value(20));
    }
}