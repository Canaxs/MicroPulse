package com.micro.product_service.service.impl;

import com.micro.product_service.dto.ProductDTO;
import com.micro.product_service.persistence.entity.Category;
import com.micro.product_service.persistence.entity.Product;
import com.micro.product_service.persistence.repository.CategoryRepository;
import com.micro.product_service.persistence.repository.ProductRepository;
import com.micro.product_service.service.ProductService;
import com.micro.product_service.specification.ProductSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDto) {
        Product product = Product.builder()
                .price(productDto.getPrice())
                .name(productDto.getName())
                .stock(productDto.getStock())
                .build();

        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            product.setCategory(category);
        }

        product = productRepository.save(product);
        return convertToDto(product);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return convertToDto(product);
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDto) {
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());

        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            product.setCategory(category);
        }

        return convertToDto(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        try {
            productRepository.deleteById(id);
        }
        catch (Exception e) {
            throw new RuntimeException("Delete failed");
        }
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAllByProductDTO();
    }

    @Override
    public List<ProductDTO> getProductsByCategoryId(Long categoryId) {
        return productRepository.findAllByCategoryAndProductDTO(categoryId);
    }

    @Override
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.filterBy(keyword);

        return productRepository.findAll(spec, pageable)
                .map(product -> new ProductDTO(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getStock(),
                        product.getCategory().getId()
                ));
    }

    @Override
    public List<ProductDTO> getProductsByStockStatus(boolean inStock) {
        if (inStock) {
            return productRepository.findByStockGreaterThan(0).stream().map(this::convertToDto).collect(Collectors.toList());
        } else {
            return productRepository.findByStock(0).stream().map(this::convertToDto).collect(Collectors.toList());
        }
    }

    @Override
    public List<ProductDTO> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min,max);
    }

    @Override
    public List<ProductDTO> getRecentProducts(int limit) {
        //return productRepository.findTopNRecent(PageRequest.of(0, limit)).stream().map(this::convertToDto).collect(Collectors.toList());
        return null;
    }

    @Override
    public void updateStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    private ProductDTO convertToDto(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .build();
    }
}
