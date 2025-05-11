package com.micro.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO implements Serializable {
    private Long id;
    private String name;
    private double price;
    private Integer stock;
    private Long categoryId;
}
