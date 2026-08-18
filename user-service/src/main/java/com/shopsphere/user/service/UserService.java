package com.shopsphere.user.service;

import com.shopsphere.user.dto.CreateUserRequest;
import com.shopsphere.user.dto.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUser(Long id);
}