package com.medical.medicalbillportal.config;

import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {


private static final String SECRET_KEY = "secret_key_12345";

// Generate Token
public String generateToken(String username) {
    return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
            .compact();
}

// Extract Username
public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}

// Extract Expiration
public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
}

// Extract single claim
public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
}

// Extract all claims
private Claims extractAllClaims(String token) {
    return Jwts.parser()
            .setSigningKey(SECRET_KEY.getBytes())
            .parseClaimsJws(token)
            .getBody();
}

// Check if token expired
private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
}

// Validate token
public Boolean validateToken(String token, String username) {
    final String extractedUsername = extractUsername(token);
    return (extractedUsername.equals(username) && !isTokenExpired(token));
}


}
