package com.micro.product_service.unit;

import com.micro.product_service.dto.CategoryDTO;
import com.micro.product_service.dto.ProductDTO;
import com.micro.product_service.persistence.entity.Category;
import com.micro.product_service.persistence.entity.Product;
import com.micro.product_service.persistence.repository.CategoryRepository;
import com.micro.product_service.persistence.repository.ProductRepository;
import com.micro.product_service.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProductServiceUnitTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository,categoryRepository);
    }


    @Test
    void create_shouldSaveProduct() {
        ProductDTO productDTO = ProductDTO.builder()
                .id(1L)
                .name("Parfüm")
                .price(200.00)
                .stock(5)
                .categoryId(1L)
                .build();
        Optional<Category> category = Optional.of(new Category(productDTO.getCategoryId(), "Erkek", null, null));
        when(categoryRepository.findById(productDTO.getCategoryId())).thenReturn(category);

        Product savedProduct = new Product(productDTO.getId(),productDTO.getName(),productDTO.getPrice(),productDTO.getStock(),category.get());
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = productService.createProduct(productDTO);

        assertNotNull(result);
        assertEquals(productDTO.getName(), result.getName());
        assertEquals(productDTO.getId(), result.getId());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());

        Product saveCaptor = captor.getValue();
        assertNotNull(saveCaptor.getCategory());
        assertEquals(productDTO.getName(), saveCaptor.getName());
        assertEquals(category.get().getId(), saveCaptor.getCategory().getId());

        verify(categoryRepository).findById(productDTO.getCategoryId());
        verify(productRepository).save(any(Product.class));
        verifyNoMoreInteractions(productRepository);

    }

    @Test
    void getProductById_shouldReturnProductDTO_whenProductExists() {
        Long id = 1L;
        Category category = new Category(1L, "Erkek", null, null);
        Optional<Product> getProduct = Optional.of(new Product(id, "Parfüm", 1, 1,category));

        when(productRepository.findById(anyLong())).thenReturn(getProduct);

        ProductDTO productDTO = productService.getProductById(id);

        assertNotNull(productDTO);
        assertEquals(getProduct.get().getName(), productDTO.getName());
        assertEquals(getProduct.get().getId(), productDTO.getId());

        verify(productRepository).findById(id);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void updateProduct_shouldUpdateProductWithValidData() {
        ProductDTO productDTO = ProductDTO.builder()
                .id(1L)
                .name("Güncellenmiş Ürün")
                .price(150.0)
                .stock(10)
                .categoryId(2L)
                .build();

        Category category = new Category(productDTO.getCategoryId(), "Kadın", null, null);

        Product existingProduct = new Product(productDTO.getId(), "Eski Ürün", 100.0, 5, null);

        Product updatedProduct = new Product(productDTO.getId(), productDTO.getName(), productDTO.getPrice(), productDTO.getStock(), category);

        when(productRepository.findById(productDTO.getId())).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(productDTO.getCategoryId())).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductDTO result = productService.updateProduct(productDTO);

        assertNotNull(result);
        assertEquals(productDTO.getId(), result.getId());
        assertEquals(productDTO.getName(), result.getName());
        assertEquals(productDTO.getPrice(), result.getPrice());
        assertEquals(productDTO.getStock(), result.getStock());
        assertEquals(productDTO.getCategoryId(), result.getCategoryId());

        verify(productRepository).findById(productDTO.getId());
        verify(categoryRepository).findById(productDTO.getCategoryId());
        verify(productRepository).save(any(Product.class));
        verifyNoMoreInteractions(productRepository, categoryRepository);
    }

    @Test
    void deleteProduct_shouldDeleteProductById() {
        Long productId = 1L;

        doNothing().when(productRepository).deleteById(productId);

        assertDoesNotThrow(() -> productService.deleteProduct(productId));

        verify(productRepository).deleteById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        List<ProductDTO> mockList = List.of(
                new ProductDTO(1L, "Ürün 1", 100, 5, 1L),
                new ProductDTO(2L, "Ürün 2", 200, 10, 2L)
        );

        when(productRepository.findAllByProductDTO()).thenReturn(mockList);

        List<ProductDTO> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Ürün 1", result.get(0).getName());

        verify(productRepository).findAllByProductDTO();
    }

    @Test
    void getProductsByCategoryId_shouldReturnProductsInGivenCategory() {
        Long categoryId = 1L;

        List<ProductDTO> mockList = List.of(
                new ProductDTO(1L, "Ürün 1", 100, 5, categoryId)
        );

        when(productRepository.findAllByCategoryAndProductDTO(categoryId)).thenReturn(mockList);

        List<ProductDTO> result = productService.getProductsByCategoryId(categoryId);

        assertEquals(1, result.size());
        assertEquals(categoryId, result.get(0).getCategoryId());

        verify(productRepository).findAllByCategoryAndProductDTO(categoryId);
    }

    @Test
    void searchProducts_shouldReturnPagedProductsMatchingKeyword() {
        String keyword = "parfüm";
        Pageable pageable = PageRequest.of(0, 10);
        Product product = new Product(1L, "parfüm", 150, 10, new Category(1L, "Kadın", null, null));

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);

        Page<ProductDTO> result = productService.searchProducts(keyword, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("parfüm", result.getContent().get(0).getName());

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProductsByStockStatus_shouldReturnInStockProducts() {
        Product product = new Product(1L, "ürün", 100, 5, new Category(1L, "Kadın", null, null));
        when(productRepository.findByStockGreaterThan(0)).thenReturn(List.of(product));

        List<ProductDTO> result = productService.getProductsByStockStatus(true);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getStock());

        verify(productRepository).findByStockGreaterThan(0);
    }

    @Test
    void getProductsByStockStatus_shouldReturnOutOfStockProducts() {
        Product product = new Product(1L, "ürün", 100, 0, new Category(1L, "Kadın", null, null));
        when(productRepository.findByStock(0)).thenReturn(List.of(product));

        List<ProductDTO> result = productService.getProductsByStockStatus(false);

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getStock());

        verify(productRepository).findByStock(0);
    }

    @Test
    void getProductsByPriceRange_shouldReturnProductsWithinRange() {
        BigDecimal min = BigDecimal.valueOf(50);
        BigDecimal max = BigDecimal.valueOf(200);

        List<ProductDTO> mockList = List.of(
                new ProductDTO(1L, "Ürün 1", 100, 10, 1L),
                new ProductDTO(2L, "Ürün 2", 150, 8, 1L)
        );

        when(productRepository.findByPriceBetween(min, max)).thenReturn(mockList);

        List<ProductDTO> result = productService.getProductsByPriceRange(min, max);

        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(p -> {
                    BigDecimal price = BigDecimal.valueOf(p.getPrice());
                    return price.compareTo(min) >= 0 && price.compareTo(max) <= 0;
                }));

        verify(productRepository).findByPriceBetween(min, max);
    }
}
