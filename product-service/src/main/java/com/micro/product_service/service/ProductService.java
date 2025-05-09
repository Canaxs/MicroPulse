package com.micro.product_service.service;

import com.micro.product_service.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductDTO createProduct(ProductDTO productDto);

    ProductDTO getProductById(Long id);

    ProductDTO updateProduct(ProductDTO productDto);

    void deleteProduct(Long id);

    List<ProductDTO> getAllProducts();

    List<ProductDTO> getProductsByCategoryId(Long categoryId);

    Page<ProductDTO> searchProducts(String keyword, Pageable pageable);

    List<ProductDTO> getProductsByStockStatus(boolean inStock);

    List<ProductDTO> getProductsByPriceRange(BigDecimal min, BigDecimal max);

    List<ProductDTO> getRecentProducts(int limit);

    void updateStock(Long productId, int quantity);

}