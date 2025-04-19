package com.milan.agents_review_api.service;

import com.milan.agents_review_api.models.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String generateToken(User user);

    String extractUsername(String token);

    Boolean validateToken(String token, UserDetails userDetails);
}
