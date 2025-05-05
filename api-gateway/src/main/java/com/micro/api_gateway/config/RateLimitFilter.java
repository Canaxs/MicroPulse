package com.micro.api_gateway.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends AbstractGatewayFilterFactory<RateLimitFilter.Config> {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String clientIp = getClientIp(exchange);

            Bucket bucket = buckets.computeIfAbsent(clientIp, this::newBucket);

            if (bucket.tryConsume(1)) {
                return chain.filter(exchange);
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }
        };
    }

    private String getClientIp(ServerWebExchange exchange) {
        String forwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwardedFor != null) {
            return forwardedFor.split(",")[0];
        } else {
            return exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();
        }

    }

    private Bucket newBucket(String key) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(1, Refill.intervally(1, Duration.ofSeconds(5))))
                .build();
    }

    public static class Config {
    }
}
