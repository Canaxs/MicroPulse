package com.micro.product_service.service;

import com.dto_common.KafkaOrderItemSendDTO;
import com.micro.product_service.annotation.LogIgnore;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumerService {

    private final OrderService orderService;

    public OrderConsumerService(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "order-topic", groupId = "log-group",containerFactory = "orderItemKafkaListenerContainerFactory")
    @LogIgnore
    public void consume(KafkaOrderItemSendDTO kafkaOrderItemSendDTO, Acknowledgment ack) {
        try {
            orderService.createOrder(kafkaOrderItemSendDTO.getItems(), kafkaOrderItemSendDTO.getUserId());
            ack.acknowledge();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
