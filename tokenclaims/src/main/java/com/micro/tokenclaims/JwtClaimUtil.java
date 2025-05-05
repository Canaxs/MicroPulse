package com.micro.tokenclaims;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.PublicKey;

public class JwtClaimUtil {
    public CustomUserDetails extractAllClaims(String token, PublicKey publicKey) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return new CustomUserDetails(claims.get("userId", String.class), claims.getSubject(), claims.get("role", String.class));
    }

    public String getUsername(Claims claims) {
        return claims.getSubject();
    }

    public String getUserId(Claims claims) {
        return claims.get("userId", String.class);
    }

    public String getRole(Claims claims) {
        return claims.get("role", String.class);
    }

}
