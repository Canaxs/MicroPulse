package com.payment_service.service;

import com.dto_common.KafkaOrderItemSendDTO;
import com.dto_common.OrderItemDTO;
import com.payment_service.annotation.LogIgnore;
import com.payment_service.dto.LogEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogProducerService {

    private final KafkaTemplate<String, LogEvent> kafkaTemplate;
    private final KafkaTemplate<String, KafkaOrderItemSendDTO> kafkaOrderTemplate;

    public LogProducerService(KafkaTemplate<String, LogEvent> kafkaTemplate, KafkaTemplate<String, KafkaOrderItemSendDTO> kafkaOrderTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaOrderTemplate = kafkaOrderTemplate;
    }

    @LogIgnore
    public void sendLog(LogEvent logEvent) {
        kafkaTemplate.send("logs-topic", logEvent);
    }

    @LogIgnore
    public void sendOrder(KafkaOrderItemSendDTO itemDTOList) {
        kafkaOrderTemplate.send("order-topic", itemDTOList);
    }
}
