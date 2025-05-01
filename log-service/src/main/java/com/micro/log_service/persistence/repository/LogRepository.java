package com.micro.log_service.persistence.repository;

import com.micro.log_service.persistence.model.Log;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends ElasticsearchRepository<Log, String> {
    List<Log> findByUsername(String username);
}
