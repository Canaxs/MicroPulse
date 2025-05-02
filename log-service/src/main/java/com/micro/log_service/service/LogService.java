package com.micro.log_service.service;

import com.micro.log_service.dto.LogEvent;

public interface LogService {
    void saveLog(LogEvent logEvent);
}
