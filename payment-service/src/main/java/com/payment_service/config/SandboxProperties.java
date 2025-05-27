package com.payment_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sandbox")
@Getter
@Setter
public class SandboxProperties {
    private String baseUrl;
    private String apiKey;
    private String secretKey;
}
