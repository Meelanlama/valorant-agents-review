package com.milan.agents_review_api.util;

import com.milan.agents_review_api.dto.UserDto;
import com.milan.agents_review_api.exceptions.ExistDataException;
import com.milan.agents_review_api.repository.RoleRepository;
import com.milan.agents_review_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Validations {

    private final UserRepository userRepo;

    private final RoleRepository roleRepo;

    //Role validation for user
    public void userValidation(UserDto userDto) {

        if(!StringUtils.hasText(userDto.getUserName())||userDto.getUserName() == null) {
            throw new IllegalArgumentException("Please enter valid username");
        }

        if(!StringUtils.hasText(userDto.getEmail()) || !userDto.getEmail().matches(Constants.EMAIL_REGEX)) {
            throw new IllegalArgumentException("Please enter valid email");
        }else {
            //validate duplicate email
            boolean exist = userRepo.existsByEmail(userDto.getEmail());
            if(exist) {
                throw new ExistDataException("Email already exists");
            }
        }

        if(CollectionUtils.isEmpty(userDto.getRoles())) {
            throw new IllegalArgumentException("Please enter valid role. Empty");
        }else {
            List<Integer> allRoleIds = roleRepo.findAll().stream().map(r -> r.getId()).collect(Collectors.toList());

            List<Integer> roleRequestId = userDto.getRoles().stream()
                    .map(r -> r.getId())
                    .filter(roleId -> allRoleIds.contains(roleId)).toList();

            if(CollectionUtils.isEmpty(roleRequestId)) {
                throw new IllegalArgumentException("Please enter valid role:" + roleRequestId);
            }
        }

    }
}
