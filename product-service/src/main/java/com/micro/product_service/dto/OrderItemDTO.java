package com.micro.product_service.dto;

import com.micro.product_service.persistence.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO implements Serializable {
    private Long id;
    private int quantity;
    private double price;
    private Long productId;
    private Long orderId;
}
