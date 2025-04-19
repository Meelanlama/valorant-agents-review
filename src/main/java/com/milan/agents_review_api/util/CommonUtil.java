package com.milan.agents_review_api.util;

import com.milan.agents_review_api.handler.GenericResponse;
import com.milan.agents_review_api.models.User;
import com.milan.agents_review_api.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

public class CommonUtil {

    public static ResponseEntity<?> createBuildResponse(Object data, HttpStatus status){

        GenericResponse response = GenericResponse.builder()
                .responseStatus(status)
                .status("Success")
                .message("Success")
                .data(data)
                .build();
        //calling create method of GenericResponse
        return response.create();
    }

    //send only message, not data. after saving show success msg only
    public static ResponseEntity<?> createBuildResponseMessage(String message, HttpStatus status) {

        GenericResponse response = GenericResponse.builder()
                .responseStatus(status)
                .status("Success")
                .message(message)
                .build();
        return response.create();
    }

    public static ResponseEntity<?> createErrorResponse(Object data,HttpStatus status){

        GenericResponse response = GenericResponse.builder()
                .responseStatus(status)
                .status("Failure")
                .message("Failure")
                .data(data)
                .build();
        //calling create method of GenericResponse
        return response.create();
    }

    public static ResponseEntity<?> createErrorResponseMessage(String message, HttpStatus status) {

        GenericResponse response = GenericResponse.builder()
                .responseStatus(status)
                .status("Failure")
                .message(message)
                .build();
        return response.create();
    }

    public static User getLoggedInUser() {
        try {
            CustomUserDetails logInUser = (CustomUserDetails)
                    SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return logInUser.getUser(); // Direct access to your full User entity
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
