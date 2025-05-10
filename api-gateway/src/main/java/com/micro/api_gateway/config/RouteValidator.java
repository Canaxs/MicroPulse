package com.micro.api_gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    public static final List<String> openApiEndpoints = List.of(
            "/user-service/user/create",
            "/user-service/auth/login",
            "/product-service/category/category-id/**",
            "/product-service/category/category-tree/**",
            "/product-service/category/root-categories",
            "/product-service/category/category-depth/**",
            "/product-service/product/by-product/**",
            "/product-service/product/all",
            "/product-service/product/by-category/**",
            "/product-service/product/search",
            "/product-service/product/stock-status",
            "/product-service/product/price-range"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> pathMatcher.match(uri, request.getURI().getPath()));
}
