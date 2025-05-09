package com.micro.product_service.specification;

import com.micro.product_service.persistence.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static final String NAME = "name";

    public static Specification<Product> filterBy(String name) {
        return Specification.where(hasName(name));
    }

    private static Specification<Product> hasName(String name) {
        return ((root, query, cb) -> name == null || name.isEmpty() ? cb.conjunction() : cb.like(root.get(NAME), "%"+name+"%"));
    }
}
