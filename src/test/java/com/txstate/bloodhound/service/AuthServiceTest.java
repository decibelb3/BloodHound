package com.txstate.bloodhound.service;

import com.txstate.bloodhound.dao.UserDao;
import com.txstate.bloodhound.model.LoginRequest;
import com.txstate.bloodhound.model.RegistrationRequest;
import com.txstate.bloodhound.model.User;
import com.txstate.bloodhound.util.OperationResult;
import com.txstate.bloodhound.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {
    @Mock
    private UserDao userDao;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userDao);
    }

    @Test
    void register_shouldSucceed_whenRequestIsValidAndUnique() {
        RegistrationRequest request = new RegistrationRequest("demo.user", "demo.user@example.com", "Pass123!");
        when(userDao.existsByUsername("demo.user")).thenReturn(false);
        when(userDao.existsByEmail("demo.user@example.com")).thenReturn(false);
        when(userDao.createUser(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(101L);
            user.setCreatedAt(LocalDateTime.now());
            return user;
        });

        OperationResult<User> result = authService.register(request);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().getPasswordHash() != null && !result.getData().getPasswordHash().isBlank());
        verify(userDao).createUser(any(User.class));
    }

    @Test
    void register_shouldRejectDuplicateUsername() {
        RegistrationRequest request = new RegistrationRequest("demo.user", "unique@example.com", "Pass123!");
        when(userDao.existsByUsername("demo.user")).thenReturn(true);
        when(userDao.existsByEmail("unique@example.com")).thenReturn(false);

        OperationResult<User> result = authService.register(request);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrors().stream().anyMatch(error -> error.contains("Username is already taken")));
    }

    @Test
    void register_shouldRejectDuplicateEmail() {
        RegistrationRequest request = new RegistrationRequest("unique-user", "demo.user@example.com", "Pass123!");
        when(userDao.existsByUsername("unique-user")).thenReturn(false);
        when(userDao.existsByEmail("demo.user@example.com")).thenReturn(true);

        OperationResult<User> result = authService.register(request);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrors().stream().anyMatch(error -> error.contains("Email is already registered")));
    }

    @Test
    void login_shouldSucceed_whenCredentialsAreValid() {
        String plainPassword = "Pass123!";
        User user = new User();
        user.setUserId(15L);
        user.setUsername("demo.user");
        user.setEmail("demo.user@example.com");
        user.setPasswordHash(PasswordUtil.hashPassword(plainPassword));

        when(userDao.findByUsername("demo.user")).thenReturn(Optional.of(user));

        OperationResult<User> result = authService.login(new LoginRequest("demo.user", plainPassword));

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().getUserId().equals(15L));
    }

    @Test
    void login_shouldFail_whenPasswordIsWrong() {
        User user = new User();
        user.setUserId(15L);
        user.setUsername("demo.user");
        user.setEmail("demo.user@example.com");
        user.setPasswordHash(PasswordUtil.hashPassword("CorrectPassword1!"));

        when(userDao.findByUsername("demo.user")).thenReturn(Optional.of(user));

        OperationResult<User> result = authService.login(new LoginRequest("demo.user", "WrongPassword1!"));

        assertFalse(result.isSuccess());
        assertTrue(result.getErrors().stream().anyMatch(error -> error.contains("Invalid credentials")));
    }
}
