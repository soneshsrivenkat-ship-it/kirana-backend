package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for handling JWT operations.
 *
 * Responsibilities:
 *  - Generate access and refresh tokens
 *  - Validate tokens
 *  - Extract claims (userId, role, etc.)
 *
 * Security Note:
 *  - Never log full tokens
 *  - Never expose secret keys
 */
@Component
public class JwtUtil {

    private static final Logger log =
            LoggerFactory.getLogger(JwtUtil.class);

    private final String SECRET =
            "bXlfc3VwZXJfc2VjcmV0X2tleV90aGF0X2lzX3ZlcnlfbG9uZ19hbmRfc2VjdXJlXzEyMzQ1Ng==";

    private final long ACCESS_EXPIRATION = 1000 * 60 * 15;      // 15 minutes
    private final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24; // 1 day

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(
                SECRET.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }

    /**
     * Generate Access Token
     */
    public String generateAccessToken(String userId, String role) {

        log.debug("Generating access token for userId: {}", userId);

        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .claim("type", "ACCESS")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate Refresh Token
     */
    public String generateRefreshToken(String username) {

        log.debug("Generating refresh token for user: {}", username);

        return Jwts.builder()
                .setSubject(username)
                .claim("type", "REFRESH")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate JWT Token
     */
    public boolean validateToken(String token) {
        try {

            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);

            log.debug("JWT token validated successfully");
            return true;

        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT token is empty or invalid: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected JWT validation error: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Extract username (subject)
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract role claim
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Extract userId (subject)
     */
    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Check if token is a refresh token
     */
    public boolean isRefreshToken(String token) {
        return "REFRESH".equals(
                extractAllClaims(token).get("type", String.class)
        );
    }

    /**
     * Extract all claims safely
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
