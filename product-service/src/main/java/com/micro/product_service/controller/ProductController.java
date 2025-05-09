package com.micro.product_service.controller;

import com.micro.product_service.dto.ProductDTO;
import com.micro.product_service.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDto) {
        return ResponseEntity.ok(productService.createProduct(productDto));
    }

    @GetMapping("/by-product/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/update")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDto) {
        return ResponseEntity.ok(productService.updateProduct(productDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategoryId(@PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(@RequestParam(required = false) String keyword, Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(keyword, pageable));
    }

    @GetMapping("/stock-status")
    public ResponseEntity<List<ProductDTO>> getProductsByStockStatus(@RequestParam Boolean stock) {
        return ResponseEntity.ok(productService.getProductsByStockStatus(stock));
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<ProductDTO>> getProductsByPriceRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return ResponseEntity.ok(productService.getProductsByPriceRange(min, max));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ProductDTO>> getRecentProducts(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(productService.getRecentProducts(limit));
    }

    @PutMapping("/update-stock")
    public ResponseEntity<Void> updateStock(@RequestParam Long productId, @RequestParam int quantity) {
        productService.updateStock(productId, quantity);
        return ResponseEntity.ok().build();
    }
}
