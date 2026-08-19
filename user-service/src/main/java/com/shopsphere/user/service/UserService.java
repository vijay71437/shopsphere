package com.shopsphere.user.service;

import com.shopsphere.user.dto.CreateUserRequest;
import com.shopsphere.user.dto.LoginRequest;
import com.shopsphere.user.dto.LoginResponse;
import com.shopsphere.user.dto.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUser(Long id);

    LoginResponse login(LoginRequest request);
}