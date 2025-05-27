package com.dto_common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaOrderItemSendDTO {
    private List<OrderItemDTO> items;
    private String userId;
}
