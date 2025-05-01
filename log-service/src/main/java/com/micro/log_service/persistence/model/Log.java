package com.micro.log_service.persistence.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "logs")
public class Log {

    @Id
    private String id;
    private String username;
    private String message;
    private String serviceName;
    private long timestamp;
}
