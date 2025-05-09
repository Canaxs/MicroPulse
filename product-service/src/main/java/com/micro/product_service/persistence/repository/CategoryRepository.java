package com.micro.product_service.persistence.repository;

import com.micro.product_service.dto.CategoryDTO;
import com.micro.product_service.persistence.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category , Long> {

    @Query("SELECT new com.micro.product_service.dto.CategoryDTO(c.id, c.name, c.parentCategory.id) " +
            "FROM Category c WHERE c.parentCategory IS NULL")
    List<CategoryDTO> findByParentCategoryIsNull();


}
