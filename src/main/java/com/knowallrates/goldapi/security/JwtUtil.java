package com.knowallrates.goldapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret:mySecretKeyForGoldRatesAPIThatIsLongEnoughForHS256Algorithm}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}") // 24 hours
    private int jwtExpirationMs;

    private SecretKey getSigningKey() {
        // Ensure the key is long enough for HS256
        if (jwtSecret.length() < 32) {
            jwtSecret = "mySecretKeyForGoldRatesAPIThatIsLongEnoughForHS256Algorithm";
        }
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String email, String role) {
        try {
            String token = Jwts.builder()
                    .setSubject(email)
                    .claim("role", role)
                    .claim("email", email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();

            System.out.println("Generated JWT token for user: " + email + " with role: " + role);
            System.out.println("Token preview: " + token.substring(0, Math.min(50, token.length())) + "...");
            return token;
        } catch (Exception e) {
            System.err.println("Error generating JWT token: " + e.getMessage());
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public String getEmailFromToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("Token is null or empty");
            }

            // Remove Bearer prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            System.err.println("Error extracting email from token: " + e.getMessage());
            throw new RuntimeException("Invalid token", e);
        }
    }

    public String getRoleFromToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("Token is null or empty");
            }

            // Remove Bearer prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("role", String.class);
        } catch (Exception e) {
            System.err.println("Error extracting role from token: " + e.getMessage());
            throw new RuntimeException("Invalid token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                System.err.println("Token validation failed: Token is null or empty");
                return false;
            }

            // Remove Bearer prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Check if token has the correct format (3 parts separated by dots)
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                System.err.println("Token validation failed: Invalid token format. Expected 3 parts, got: " + parts.length);
                return false;
            }

            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            System.out.println("Token validation successful");
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("Token validation failed: Token expired - " + e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            System.err.println("Token validation failed: Unsupported JWT - " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            System.err.println("Token validation failed: Malformed JWT - " + e.getMessage());
            return false;
        } catch (SignatureException e) {
            System.err.println("Token validation failed: Invalid signature - " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("Token validation failed: Illegal argument - " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return true;
            }

            // Remove Bearer prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            boolean expired = expiration.before(new Date());
            if (expired) {
                System.out.println("Token is expired. Expiration: " + expiration + ", Current: " + new Date());
            }
            return expired;
        } catch (Exception e) {
            System.err.println("Error checking token expiration: " + e.getMessage());
            return true;
        }
    }
}
