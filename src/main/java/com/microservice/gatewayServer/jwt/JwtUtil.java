package com.microservice.gatewayServer.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract a specific claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Check if token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    // Validate token
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        // Assuming roles are stored as a list in the "roles" claim
        Object rolesObject = claims.get("roles");

        if (rolesObject instanceof List<?> rolesList) { // Java 16+ syntax (pattern matching)
            return rolesList.stream()
                    .map(Object::toString) // Convert each role safely to String
                    .toList();
        }

        return List.of(); // Return empty list if no roles found
    }
}
