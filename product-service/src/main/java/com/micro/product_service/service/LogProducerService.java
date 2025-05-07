package com.micro.product_service.service;


import com.micro.product_service.annotation.LogIgnore;
import com.micro.product_service.dto.LogEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogProducerService {

    private final KafkaTemplate<String, LogEvent> kafkaTemplate;

    public LogProducerService(KafkaTemplate<String, LogEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @LogIgnore
    public void sendLog(LogEvent logEvent) {
        kafkaTemplate.send("logs-topic", logEvent);
    }
}
