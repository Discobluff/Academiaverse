package com.academiaverse.security;

import com.academiaverse.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final SecretKey secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Instant now = Instant.now();
        return extractClaims(token).getExpiration().before(Date.from(now));
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .claim("role", userDetails.getAuthorities().iterator().next().getAuthority())
            .compact();
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(user.getUsername())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .claim("role", user.getRole())
            .claim("userId", user.getId())
            .compact();
    }
}
