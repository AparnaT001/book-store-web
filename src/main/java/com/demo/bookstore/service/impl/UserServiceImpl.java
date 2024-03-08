package com.demo.bookstore.service.impl;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.bookstore.entity.Role;
import com.demo.bookstore.entity.User;
import com.demo.bookstore.exception.BookstoreException;
import com.demo.bookstore.models.request.UserRequest;
import com.demo.bookstore.models.response.UserResponse;
import com.demo.bookstore.repository.RoleRepository;
import com.demo.bookstore.repository.UserRepository;
import com.demo.bookstore.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	public static final String USER_ALREADY_EXISTS = "User already exists!";
	public static final String INVALID_ROLE = "Invalid Role!!";

	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;

	@Override
	public UserResponse register(UserRequest userRequest) {
		UserResponse userResponse = new UserResponse();
		Optional<User> userOptional = userRepository.findByMailId(userRequest.getMailId());
		if (userOptional.isPresent()) {
			throw new BookstoreException(USER_ALREADY_EXISTS);
		}
		Optional<Role> roleOptional = roleRepository.findByRoleNameIgnoreCase(userRequest.getRoleName());
		if (!roleOptional.isPresent()) {
			throw new BookstoreException(INVALID_ROLE);
		}
		User user = new User();
		BeanUtils.copyProperties(userRequest, user);
		user.setRole(roleOptional.get());
		user = userRepository.save(user);
		BeanUtils.copyProperties(user, userResponse);
		userResponse.setRoleId(user.getRole().getId());
		return userResponse;
	}
}
