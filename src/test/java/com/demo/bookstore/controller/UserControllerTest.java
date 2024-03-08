package com.demo.bookstore.controller;

import com.demo.bookstore.exception.BookstoreException;
import com.demo.bookstore.models.request.UserRequest;
import com.demo.bookstore.models.response.AppResponse;
import com.demo.bookstore.models.response.Response;
import com.demo.bookstore.models.response.UserResponse;
import com.demo.bookstore.service.UserService;
import com.demo.bookstore.utility.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    BindingResult bindingResult;

    @InjectMocks
    private UserController userController;

    private UserRequest validUserRequest;

    @Before
    public void setUp() {
        validUserRequest = new UserRequest();
        validUserRequest.setFirstName("John");
        validUserRequest.setLastName("Doe");
        validUserRequest.setMailId("john.doe@example.com");
        validUserRequest.setRoleName("Admin");
    }

    @Test
    public void testRegisterUser_Success() {
        UserResponse userResponse = new UserResponse();
        userResponse.setFirstName("John");
        userResponse.setLastName("Doe");
        userResponse.setMailId("john.doe@example.com");
        userResponse.setRoleId(1L);

        when(userService.register(validUserRequest)).thenReturn(userResponse);

        ResponseEntity<Response> responseEntity = userController.registerUser(validUserRequest, bindingResult);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("User registered successfully ", ((AppResponse<?>) responseEntity.getBody()).getMessage());
        assertEquals(userResponse, ((AppResponse<?>) responseEntity.getBody()).getResponse());
    }

    @Test
    public void testRegisterUser_ValidationFailure() {
        UserRequest invalidUserRequest = new UserRequest();

        when(bindingResult.hasErrors()).thenReturn(true);
        ResponseEntity<Response> responseEntity = userController.registerUser(invalidUserRequest, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Constants.INVALID_PAYLOAD, ((AppResponse<?>) responseEntity.getBody()).getMessage());
    }

    @Test(expected = BookstoreException.class)
    public void testRegisterUser_BookstoreException() {
        // Mocking userService to throw an exception
        when(userService.register(validUserRequest)).thenThrow(new BookstoreException("User registration failed"));

        userController.registerUser(validUserRequest, bindingResult);
    }

}
