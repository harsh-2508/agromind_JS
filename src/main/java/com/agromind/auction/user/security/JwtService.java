package com.agromind.auction.user.security;

import com.agromind.auction.user.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // A secure 256-bit secret key used to sign the tokens.
    // (In production, this lives in your environment variables, never hardcoded!)
    private static final String SECRET = "8z/X3/tP8R2w4bH6qN5xT9vL1mY4kC2jF7uG9dZ3wQ8=";
    // Token is valid for 24 hours
    private static final long EXPIRATION_TIME = 86400000;

    private Key getSigningKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail()) // The main identifier
                .claim("role", user.getRole().name()) // Add custom data like role
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
