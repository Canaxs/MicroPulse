package com.micro.log_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogEvent {
    private String message;
    private String serviceName;
    private String serviceURL;
    private String statusCode;
    private LocalDateTime logDate;
}
