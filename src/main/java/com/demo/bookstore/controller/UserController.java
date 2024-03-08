package com.demo.bookstore.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.demo.bookstore.config.SwaggerConfig;
import com.demo.bookstore.exception.BookstoreException;
import com.demo.bookstore.models.request.UserRequest;
import com.demo.bookstore.models.response.AppResponse;
import com.demo.bookstore.models.response.Response;
import com.demo.bookstore.models.response.UserResponse;
import com.demo.bookstore.service.UserService;
import com.demo.bookstore.utility.AppUtility;
import com.demo.bookstore.utility.Constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("user/")
@Tag(name = SwaggerConfig.USER_TAG)
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	@Operation(description = "API to register new user")
	@PostMapping("register")
	public ResponseEntity<Response> registerUser(@Valid @RequestBody UserRequest input,BindingResult bindingResult) {
		try {
			AppResponse<UserResponse> response = new AppResponse<>();
			if (bindingResult.hasErrors()) {
				response = new AppResponse<>(HttpStatus.BAD_REQUEST.value(), Constants.INVALID_PAYLOAD);
				AppUtility.setErrorMap(bindingResult, response);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			UserResponse userResponse = userService.register(input);
			response.setMessage("User registered successfully ");
			response.setStatus(HttpStatus.OK.value());
			response.setResponse(userResponse);
			return ResponseEntity.ok(response);
		} catch (BookstoreException e) {
			log.error("Error :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Error while register new user", e);
			throw new BookstoreException("Error while register new user", e);
		}
	}

	@ExceptionHandler(BookstoreException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	ResponseEntity<Response> handleResponseStatusException(BookstoreException ex) {
		Response response = AppUtility.getStoreExceptionResponse(ex);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}