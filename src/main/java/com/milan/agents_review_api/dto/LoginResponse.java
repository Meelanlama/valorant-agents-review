package com.milan.agents_review_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private UserResponse user;

    private String token;

}
