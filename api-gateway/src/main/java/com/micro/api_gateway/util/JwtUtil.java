package com.micro.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
public class JwtUtil {

    public boolean validateToken(String token , PublicKey publicKey) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new java.util.Date());
        } catch (Exception e) {
            System.out.println("JWT validation failed: " + e.getMessage());
            return false;
        }
    }
    public String extractUsername(String token , PublicKey publicKey) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
