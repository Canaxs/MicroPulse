package com.micro.log_service.persistence.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@Data
@Document(indexName = "logs")
public class Log {

    @Id
    private String id;

    private String message;
    private String serviceName;
    private String serviceURL;
    private String statusCode;
    private LocalDateTime logDate;
    private LocalDateTime timestamp;
}
