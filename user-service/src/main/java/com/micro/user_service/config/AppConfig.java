package com.micro.user_service.config;

import com.micro.tokenclaims.JwtClaimUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public JwtClaimUtil jwtClaimUtil() {
        return new JwtClaimUtil();
    }
}
