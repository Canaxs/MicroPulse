package com.micro.api_gateway.config;

import com.micro.api_gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.RouteMatcher;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class JwtAuthenticationFilter implements WebFilter {
    private final PublicKey publicKey;

    private final RouteValidator validator;

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil) throws Exception {
        this.validator = validator;
        this.jwtUtil = jwtUtil;
        try (InputStream is = new ClassPathResource("keys/public_key.pem").getInputStream()) {
            String key = new String(is.readAllBytes())
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            this.publicKey = kf.generatePublic(new X509EncodedKeySpec(decoded));
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (validator.isSecured.test(exchange.getRequest().getPath())) {
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);

                if (jwtUtil.validateToken(token, publicKey)) {
                    exchange.getAttributes().put("username", jwtUtil.extractUsername(token, publicKey));
                } else {
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token"));
                }
            } else {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header missing or invalid"));
            }
        }

        return chain.filter(exchange);
    }
}
