package com.micro.product_service.integration.service;

import com.micro.product_service.dto.ProductDTO;
import com.micro.product_service.persistence.entity.Category;
import com.micro.product_service.persistence.entity.Product;
import com.micro.product_service.persistence.repository.CategoryRepository;
import com.micro.product_service.persistence.repository.ProductRepository;
import com.micro.product_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void createProduct_shouldPersistAndReturnProductDTO() {
        Category category = new Category();
        category.setName("Elektronik");
        category = categoryRepository.save(category);

        ProductDTO dto = new ProductDTO(null,"Laptop",25000.0,10,category.getId());

        ProductDTO saved = productService.createProduct(dto);

        assertNotNull(saved.getId());
        assertEquals("Laptop", saved.getName());
    }

    @Test
    void getProductById_shouldReturnProduct() {
        Category category = new Category();
        category.setName("Moda");
        category = categoryRepository.save(category);

        Product product = new Product(null,"T-shirt",100.0,30,category);
        product = productRepository.save(product);

        ProductDTO found = productService.getProductById(product.getId());

        assertEquals(product.getId(), found.getId());
        assertEquals("T-shirt", found.getName());
    }

    @Test
    void updateProduct_shouldUpdateFields() {
        Category category = new Category();
        category.setName("Ev");
        category = categoryRepository.save(category);

        Product product = new Product(null,"Masa",500.0,4,category);
        product = productRepository.save(product);

        ProductDTO dto = new ProductDTO(product.getId(),"YemekMasası",750.0,8, category.getId());

        ProductDTO updated = productService.updateProduct(dto);

        assertEquals("Yemek Masası", updated.getName());
        assertEquals(750.0, updated.getPrice());
        assertEquals(8, updated.getStock());
    }

    @Test
    void deleteProduct_shouldRemoveIt() {
        Category category = new Category();
        category.setName("Oyun");
        category = categoryRepository.save(category);

        Product product = new Product(null,"Joystick",300.0,12,category);
        product = productRepository.save(product);

        productService.deleteProduct(product.getId());

        assertFalse(productRepository.findById(product.getId()).isPresent());
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        Category c1 = new Category();
        c1.setName("Aksesuar");
        c1 = categoryRepository.save(c1);

        Product p1 = new Product(null,"Saat",200.0,5,c1);
        productRepository.save(p1);

        Product p2 = new Product(null,"Kemer",150.0,10,c1);
        productRepository.save(p2);

        List<ProductDTO> all = productService.getAllProducts();

        assertTrue(all.size() >= 2);
    }

    @Test
    void getProductsByCategoryId_shouldReturnCorrectProducts() {
        Category category = new Category();
        category.setName("Kitap");
        category = categoryRepository.save(category);

        Product p1 = new Product(null,"Roman",90.0,5,category);
        productRepository.save(p1);

        Product p2 = new Product(null,"Hikaye",60.0,3,category);
        productRepository.save(p2);

        List<ProductDTO> result = productService.getProductsByCategoryId(category.getId());

        assertEquals(2, result.size());
    }

    @Test
    void searchProducts_shouldReturnFilteredResults() {
        Category category = new Category();
        category.setName("Ofis");
        category = categoryRepository.save(category);

        Product p = new Product(null,"Sandalye",1200.0,7,category);
        productRepository.save(p);

        var page = productService.searchProducts("sandalye", PageRequest.of(0, 10));

        assertTrue(page.getTotalElements() >= 1);
        assertTrue(page.getContent().get(0).getName().toLowerCase().contains("sandalye"));
    }

    @Test
    void getProductsByStockStatus_shouldReturnCorrectly() {
        Category category = new Category();
        category.setName("Market");
        category = categoryRepository.save(category);

        Product inStock = new Product(null,"Süt",25.0,10,category);
        productRepository.save(inStock);

        Product outStock = new Product(null,"Yumurta",30.0,0,category);
        productRepository.save(outStock);

        List<ProductDTO> inStockList = productService.getProductsByStockStatus(true);
        List<ProductDTO> outStockList = productService.getProductsByStockStatus(false);

        assertTrue(inStockList.stream().anyMatch(p -> p.getName().equals("Süt")));
        assertTrue(outStockList.stream().anyMatch(p -> p.getName().equals("Yumurta")));
    }

    @Test
    void getProductsByPriceRange_shouldReturnWithinRange() {
        Category category = new Category();
        category.setName("Elektronik");
        category = categoryRepository.save(category);

        Product p = new Product(null,"Klavye",250.0,6,category);
        productRepository.save(p);

        List<ProductDTO> result = productService.getProductsByPriceRange(BigDecimal.valueOf(200), BigDecimal.valueOf(300));

        assertTrue(result.stream().anyMatch(prod -> prod.getName().equals("Klavye")));
    }

    @Test
    void updateStock_shouldDecreaseStockProperly() {
        Category category = new Category();
        category.setName("Gıda");
        category = categoryRepository.save(category);

        Product p = new Product(null,"Un",45.0,20,category);
        p = productRepository.save(p);

        productService.updateStock(p.getId(), 5);

        Product updated = productRepository.findById(p.getId()).orElseThrow();
        assertEquals(15, updated.getStock());
    }
}
