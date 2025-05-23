package com.micro.log_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@EnableDiscoveryClient
@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.micro.log_service.persistence.repository")
public class LogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogServiceApplication.class, args);
	}

}
