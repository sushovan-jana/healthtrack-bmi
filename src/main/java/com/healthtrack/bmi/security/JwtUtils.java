package com.healthtrack.bmi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    public static final String COOKIE_NAME = "JWT-TOKEN";

    @Value("${healthtrack.jwt.secret}")
    private String jwtSecret;

    @Value("${healthtrack.jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Generates a signed JWT token containing the doctor's email.
     */
    public String generateToken(String email) {
        SecretKey key = getSigningKey();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the JWT token from request cookies or Authorization Bearer header.
     * Cookie takes priority; Authorization header is used as fallback for cross-domain requests.
     */
    public String resolveToken(HttpServletRequest request) {
        // 1. Try cookie first (same-domain / SameSite=None)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        // 2. Fallback: Authorization: Bearer <token> header (cross-domain localStorage approach)
        String bearerHeader = request.getHeader("Authorization");
        if (bearerHeader != null && bearerHeader.startsWith("Bearer ")) {
            return bearerHeader.substring(7);
        }
        return null;
    }

    /**
     * Extracts the subject (email) from the token.
     */
    public String getEmailFromToken(String token) {
        SecretKey key = getSigningKey();
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validates the structure and signature of a JWT token.
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = getSigningKey();
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // SignatureException, ExpiredJwtException, MalformedJwtException, etc.
            return false;
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
