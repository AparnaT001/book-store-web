package com.demo.bookstore.service;

import com.demo.bookstore.entity.Role;
import com.demo.bookstore.entity.User;
import com.demo.bookstore.exception.BookstoreException;
import com.demo.bookstore.repository.UserRepository;
import com.demo.bookstore.service.impl.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAuthenticateWithAdminRole() {
        // Mock data
        User user = new User();
        Role role = new Role();
        role.setRoleName("Admin");
        user.setRole(role);
        when(userRepository.findByMailId("admin@example.com")).thenReturn(Optional.of(user));

        // Invoke the method under test
        authenticationService.authenticate("admin@example.com");

        // Verify interactions
        verify(userRepository, times(1)).findByMailId("admin@example.com");
    }

    @Test(expected = BookstoreException.class)
    public void testAuthenticateWithNonAdminRole() {
        // Mock data
        User user = new User();
        Role role = new Role();
        role.setRoleName("User");
        user.setRole(role);
        when(userRepository.findByMailId("user@example.com")).thenReturn(Optional.of(user));

        // Invoke the method under test
        authenticationService.authenticate("user@example.com");
    }

    @Test(expected = BookstoreException.class)
    public void testAuthenticateWithInvalidUser() {
        // Mock data
        when(userRepository.findByMailId("invalid@example.com")).thenReturn(Optional.empty());

        // Invoke the method under test
        authenticationService.authenticate("invalid@example.com");
    }

    @Test(expected = BookstoreException.class)
    public void testAuthenticateWithNullRole() {
        // Mock data
        User user = new User();
        when(userRepository.findByMailId("nullrole@example.com")).thenReturn(Optional.of(user));

        // Invoke the method under test
        authenticationService.authenticate("nullrole@example.com");
    }

    @Test
    public void testValidateWithAdminRole() {
        // Mock data
        User user = new User();
        Role role = new Role();
        role.setRoleName("Admin");
        user.setRole(role);
        when(userRepository.findByMailId("admin@example.com")).thenReturn(Optional.of(user));

        // Invoke the method under test
        boolean result = authenticationService.validate("admin@example.com");

        // Verify result
        assertTrue(result);

        // Verify interactions
        verify(userRepository, times(1)).findByMailId("admin@example.com");
    }

    @Test
    public void testValidateWithNonAdminRole() {
        // Mock data
        User user = new User();
        Role role = new Role();
        role.setRoleName("User");
        user.setRole(role);
        when(userRepository.findByMailId("user@example.com")).thenReturn(Optional.of(user));

        // Invoke the method under test
        boolean result = authenticationService.validate("user@example.com");

        // Verify result
        assertFalse(result);

        // Verify interactions
        verify(userRepository, times(1)).findByMailId("user@example.com");
    }

    @Test(expected = BookstoreException.class)
    public void testValidateWithInvalidUser() {
        // Mock data
        when(userRepository.findByMailId("invalid@example.com")).thenReturn(Optional.empty());

        // Invoke the method under test
        authenticationService.validate("invalid@example.com");
    }

    @Test
    public void testValidateWithNullRole() {
        // Mock data
        User user = new User();
        when(userRepository.findByMailId("nullrole@example.com")).thenReturn(Optional.of(user));

        // Invoke the method under test
        boolean result = authenticationService.validate("nullrole@example.com");

        // Verify result
        assertFalse(result);

        // Verify interactions
        verify(userRepository, times(1)).findByMailId("nullrole@example.com");
    }
}
