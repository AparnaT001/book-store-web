package com.demo.bookstore.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.bookstore.entity.User;
import com.demo.bookstore.exception.BookstoreException;
import com.demo.bookstore.repository.UserRepository;

@Service
public class AuthenticationService {

	@Autowired
	private UserRepository userRepository;

	private static final String ADMIN_ROLE_NAME = "Admin";

	public void authenticate(String userId) {
		if (!validate(userId)) {
			throw new BookstoreException("Insufficient privilege to modify resource");
		}
	}

	public boolean validate(String mailId) {
		Optional<User> userOptional = userRepository.findByMailId(mailId);
		if (!userOptional.isPresent()) {
			throw new BookstoreException("User not found");
		}
		User user = userOptional.get();
		if (user.getRole() != null && user.getRole().getRoleName() != null) {
			return user.getRole().getRoleName().equalsIgnoreCase(ADMIN_ROLE_NAME);
		}
		return false;
	}

}
