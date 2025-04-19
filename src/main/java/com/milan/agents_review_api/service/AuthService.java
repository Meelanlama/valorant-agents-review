package com.milan.agents_review_api.service;

import com.milan.agents_review_api.dto.LoginRequest;
import com.milan.agents_review_api.dto.LoginResponse;
import com.milan.agents_review_api.dto.UserDto;

public interface AuthService {

    Boolean registerUser(UserDto userDto) throws Exception;

    LoginResponse login(LoginRequest loginRequest);

    LoginResponse refreshToken(String refreshToken);

}
