package com.demo.bookstore.service;

import com.demo.bookstore.entity.Role;
import com.demo.bookstore.entity.User;
import com.demo.bookstore.exception.BookstoreException;
import com.demo.bookstore.models.request.UserRequest;
import com.demo.bookstore.models.response.UserResponse;
import com.demo.bookstore.repository.RoleRepository;
import com.demo.bookstore.repository.UserRepository;
import com.demo.bookstore.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegisterUser() {
        // Mock data
        UserRequest userRequest = new UserRequest();
        userRequest.setMailId("test@example.com");
        userRequest.setRoleName("USER");

        Role role = new Role();
        role.setId(1L);
        role.setRoleName("USER");
        User user = new User();
        user.setId(1L);
        user.setMailId("test@example.com");
        user.setRole(role);

        when(userRepository.findByMailId("test@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByRoleNameIgnoreCase("USER")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Invoke the method under test
        UserResponse userResponse = userService.register(userRequest);

        // Verify result
        assertNotNull(userResponse);
        assertEquals("test@example.com", userResponse.getMailId());
        assertEquals(Long.valueOf(1), userResponse.getRoleId());

        // Verify interactions
        verify(userRepository, times(1)).findByMailId("test@example.com");
        verify(roleRepository, times(1)).findByRoleNameIgnoreCase("USER");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test(expected = BookstoreException.class)
    public void testRegisterUserWithExistingEmail() {
        // Mock data
        UserRequest userRequest = new UserRequest();
        userRequest.setMailId("test@example.com");

        when(userRepository.findByMailId("test@example.com")).thenReturn(Optional.of(new User()));

        // Invoke the method under test
        userService.register(userRequest);
    }

    @Test(expected = BookstoreException.class)
    public void testRegisterUserWithInvalidRole() {
        // Mock data
        UserRequest userRequest = new UserRequest();
        userRequest.setRoleName("INVALID_ROLE");

        when(roleRepository.findByRoleNameIgnoreCase("INVALID_ROLE")).thenReturn(Optional.empty());

        // Invoke the method under test
        userService.register(userRequest);
    }
}
