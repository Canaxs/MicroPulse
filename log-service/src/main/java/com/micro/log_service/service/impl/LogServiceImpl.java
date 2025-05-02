package com.micro.log_service.service.impl;

import com.micro.log_service.dto.LogEvent;
import com.micro.log_service.persistence.model.Log;
import com.micro.log_service.service.LogService;
import org.springframework.stereotype.Service;
import com.micro.log_service.persistence.repository.LogRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public void saveLog(LogEvent logEvent) {
        Log log = new Log();
        log.setId(UUID.randomUUID().toString());
        log.setMessage(logEvent.getMessage());
        log.setServiceName(logEvent.getServiceName());
        log.setServiceURL(logEvent.getServiceURL());
        log.setStatusCode(logEvent.getStatusCode());
        log.setLogDate(logEvent.getLogDate());
        log.setTimestamp(LocalDateTime.now());

        logRepository.save(log);
    }
}
