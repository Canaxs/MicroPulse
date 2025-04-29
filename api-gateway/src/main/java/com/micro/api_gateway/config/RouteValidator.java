package com.micro.api_gateway.config;

import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    public static final List<String> openApiEndpoints = List.of(
            "/user-service/user/create",
            "/user-service/auth/login"
    );

    public Predicate<RequestPath> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.toString().startsWith(uri));
}
