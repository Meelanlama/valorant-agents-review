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

           String token = jwtService.generateToken(customUserDetails.getUser());

           return LoginResponse.builder()
                   .user(mapper.map(customUserDetails.getUser(), UserResponse.class))
                   .token(token).build();
        }
        return null;
    }
}
