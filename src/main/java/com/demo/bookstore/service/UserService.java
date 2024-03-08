package com.demo.bookstore.service;

import com.demo.bookstore.models.request.UserRequest;
import com.demo.bookstore.models.response.UserResponse;

public interface UserService {

	UserResponse register(UserRequest user);
}