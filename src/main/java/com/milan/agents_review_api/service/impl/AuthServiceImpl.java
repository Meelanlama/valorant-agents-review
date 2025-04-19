package com.milan.agents_review_api.service.impl;

import com.milan.agents_review_api.dto.LoginRequest;
import com.milan.agents_review_api.dto.LoginResponse;
import com.milan.agents_review_api.dto.UserDto;
import com.milan.agents_review_api.dto.UserResponse;
import com.milan.agents_review_api.models.Role;
import com.milan.agents_review_api.models.User;
import com.milan.agents_review_api.repository.RoleRepository;
import com.milan.agents_review_api.repository.UserRepository;
import com.milan.agents_review_api.security.CustomUserDetails;
import com.milan.agents_review_api.service.AuthService;
import com.milan.agents_review_api.service.JwtService;
import com.milan.agents_review_api.util.Validations;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;

    private final ModelMapper mapper;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepo;

    private final Validations validation;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;


    @Override
    public Boolean registerUser(UserDto userDto) throws Exception {

        //validate role before register
        validation.userValidation(userDto);
        User saveUser = mapper.map(userDto, User.class);

        setRole(userDto,saveUser);

        saveUser.setPassword(passwordEncoder.encode(saveUser.getPassword()));

            // Save the user and proceed
            userRepo.save(saveUser);
            return true; // Return true indicating success
    }

    private void setRole(UserDto userDto,User saveUser) {
        List<Integer> roleId = userDto.getRoles()
                .stream()
                .map(r -> r.getId()).toList();
        List<Role> roles = roleRepo.findAllById(roleId);
        saveUser.setRoles(roles);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                (loginRequest.getEmail(), loginRequest.getPassword()));

        if(authenticate.isAuthenticated()) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authenticate.getPrincipal();
            User user = customUserDetails.getUser();

           String token = jwtService.generateToken(user);
           String refreshToken = jwtService.generateRefreshToken(user); // Separate refresh token

            return LoginResponse.builder()
                   .user(mapper.map(customUserDetails.getUser(), UserResponse.class))
                   .accessToken(token)
                   .refreshToken(refreshToken).build();
        }
        return null;
    }
    @Override
    public LoginResponse refreshToken(String refreshToken) {

        // Extract the username (email) from the refresh token
        String username = jwtService.extractUsername(refreshToken);

        // Retrieve the user from the database
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Load the UserDetails object for token validation
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Validate the refresh token using the UserDetails object
        boolean isValid = jwtService.validateToken(refreshToken, userDetails);

        if (!isValid) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        // Generate a new access token and optional new refresh token (token rotation)
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // Return the response with the new tokens
        return LoginResponse.builder()
                .user(mapper.map(user, UserResponse.class))
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }


}
