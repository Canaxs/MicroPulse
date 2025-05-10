package com.micro.product_service.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityContextJwtFilter securityContextJwtFilter;

    public SecurityConfig(SecurityContextJwtFilter securityContextJwtFilter) {
        this.securityContextJwtFilter = securityContextJwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(x ->
                        x.requestMatchers(
                                        "/category/category-id/**",
                                        "/category/category-tree/**",
                                        "/category/root-categories",
                                        "/category/category-depth/**",
                                        "/product/by-product/**",
                                        "/product/all",
                                        "/product/by-category/**",
                                        "/product/search",
                                        "/product/stock-status",
                                        "/product/price-range"
                        ).permitAll()
                                .requestMatchers(
                                        "/category/create",
                                        "/category/update",
                                        "/category/delete/**",
                                        "/product/create",
                                        "/product/update",
                                        "/product/delete/**",
                                        "/product/update-stock",
                                        "/order/create",
                                        "/order/update",
                                        "/order/delete/**",
                                        "/order/add-items/**",
                                        "/order/total-amount/**",
                                        "/order/by-user",
                                        "/order/items/**",
                                        "/order/update-item/**",
                                        "/order/remove-item/**"
                        ).authenticated()
                )
                .sessionManagement(x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(securityContextJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
