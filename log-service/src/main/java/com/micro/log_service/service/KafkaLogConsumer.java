package com.micro.log_service.service;

import com.micro.log_service.dto.LogEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaLogConsumer {

    private final LogService logService;

    public KafkaLogConsumer(LogService logService) {
        this.logService = logService;
    }

    @KafkaListener(topics = "logs-topic", groupId = "log-group")
    public void consume(LogEvent logEvent) {
        logService.saveLog(logEvent);
    }
}
