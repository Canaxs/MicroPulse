package com.micro.tokenclaims;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class JwtClaimUtil {

    private final PublicKey publicKey;

    public JwtClaimUtil(){
        try (InputStream is = new ClassPathResource("keys/public_key.pem").getInputStream()) {
            String key = new String(is.readAllBytes())
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            this.publicKey = kf.generatePublic(new X509EncodedKeySpec(decoded));
        }
        catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public CustomUserDetails extractAllClaims(String token) {
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
