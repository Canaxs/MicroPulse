package com.micro.log_service.persistence.repository;

import com.micro.log_service.persistence.model.Log;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LogRepository extends ElasticsearchRepository<Log, String> {
}
