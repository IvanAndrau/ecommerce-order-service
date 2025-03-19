package com.example.ecommerce_order_service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final SecretKey signingKey;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract roles from token
    public List<String> extractRoles(String token) {
        Claims claims = extractClaims(token);
        return claims.get("roles", List.class);
    }

    // Extract Claims from Token
    public Claims extractClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extract a specific claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    // Validate Token
    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username);
    }
}
