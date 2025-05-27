package com.dto_common;

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
