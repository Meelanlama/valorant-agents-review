package com.milan.agents_review_api.service.impl;

import com.milan.agents_review_api.models.User;
import com.milan.agents_review_api.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;


    @Override
    public String generateToken(User user) {
       Map<String, Object> claims = new HashMap<>();
       claims.put("id", user.getId());
       claims.put("roles", user.getRoles());

       String token = Jwts.builder()
               .claims().add(claims)
               .subject(user.getEmail())
               .issuedAt(new Date(System.currentTimeMillis()))
               .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 mins
               .and().signWith(getKey())
               .compact();

       return token;
    }

    @Override
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("roles", user.getRoles());

        String refreshToken = Jwts.builder()
                .claims().add(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 days
                .and().signWith(getKey())
                .compact();

        return refreshToken;
    }

    public Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(decryptKey(secretKey))
                    .build().parseSignedClaims(token).getPayload();
        }catch (Exception e) {
            throw e;
        }
    }

    private SecretKey decryptKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String extractUsername(String token) {
       Claims claims = getClaimsFromToken(token);
       return claims.getSubject();
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        Boolean isExpired = isTokenExpired(token);

        if(username.equals(userDetails.getUsername()) && !isExpired) {
            return true;
        }
        return false;
    }

    private Boolean isTokenExpired(String token) {
        Claims claims = getClaimsFromToken(token);
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
