package com.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogEvent {
    private String message;
    private String serviceName;
    private String serviceURL;
    private String statusCode;
    private LocalDateTime logDate;
}
