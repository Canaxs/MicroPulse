package com.micro.product_service.persistence.repository;

import com.micro.product_service.dto.ProductDTO;
import com.micro.product_service.persistence.entity.Product;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product , Long> , JpaSpecificationExecutor<Product> {

    @Query("SELECT new com.micro.product_service.dto.ProductDTO(p.id,p.name ,p.price , p.stock , p.category.id) FROM Product p")
    List<ProductDTO> findAllByProductDTO();

    @Query("SELECT new com.micro.product_service.dto.ProductDTO(p.id,p.name ,p.price , p.stock , p.category.id) " +
            "FROM Product p WHERE p.category.id =:categoryId ")
    List<ProductDTO> findAllByCategoryAndProductDTO(@Param("categoryId") Long categoryId);

    Page<Product> findAll(@Nullable Specification<Product> spec, @NonNull Pageable pageable);

    List<Product> findByStockGreaterThan(int stock);

    List<Product> findByStock(int stock);

    @Query("SELECT new com.micro.product_service.dto.ProductDTO(p.id, p.name, p.price, p.stock, p.category.id) " +
            "FROM Product p WHERE p.price BETWEEN :min AND :max")
    List<ProductDTO> findByPriceBetween(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
}
