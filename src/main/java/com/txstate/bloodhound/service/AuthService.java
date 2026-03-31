package com.txstate.bloodhound.service;

import com.txstate.bloodhound.dao.UserDao;
import com.txstate.bloodhound.model.LoginRequest;
import com.txstate.bloodhound.model.RegistrationRequest;
import com.txstate.bloodhound.model.User;
import com.txstate.bloodhound.util.OperationResult;
import com.txstate.bloodhound.util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Coordinates user registration and login flows.
 */
public class AuthService {
    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Registers a new account using validated request data.
     *
     * @param request registration request
     * @return success or failure result with created user payload
     */
    public OperationResult<User> register(RegistrationRequest request) {
        List<String> errors = validateRegistrationRequest(request);
        if (!errors.isEmpty()) {
            return OperationResult.failure("Registration failed.", errors);
        }

        String normalizedUsername = normalize(request.getUsername());
        String normalizedEmail = normalize(request.getEmail());

        if (userDao.existsByUsername(normalizedUsername)) {
            errors.add("Username is already taken.");
        }
        if (userDao.existsByEmail(normalizedEmail)) {
            errors.add("Email is already registered.");
        }
        if (!errors.isEmpty()) {
            return OperationResult.failure("Registration failed.", errors);
        }

        User user = new User();
        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        user.setPasswordHash(PasswordUtil.hashPassword(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        User createdUser = userDao.createUser(user);
        return OperationResult.success("Registration successful.", createdUser);
    }

    /**
     * Authenticates a user using username/email and password.
     *
     * @param request login request
     * @return success or failure result with authenticated user payload
     */
    public OperationResult<User> login(LoginRequest request) {
        List<String> errors = validateLoginRequest(request);
        if (!errors.isEmpty()) {
            return OperationResult.failure("Login failed.", errors);
        }

        String usernameOrEmail = normalize(request.getUsernameOrEmail());
        Optional<User> userOptional = usernameOrEmail.contains("@")
                ? userDao.findByEmail(usernameOrEmail)
                : userDao.findByUsername(usernameOrEmail);

        if (userOptional.isEmpty()) {
            return OperationResult.failure("Login failed.", List.of("Invalid credentials."));
        }

        User user = userOptional.get();
        boolean validPassword = PasswordUtil.verifyPassword(request.getPassword(), user.getPasswordHash());
        if (!validPassword) {
            return OperationResult.failure("Login failed.", List.of("Invalid credentials."));
        }

        return OperationResult.success("Login successful.", user);
    }

    /**
     * Looks up user profile details.
     *
     * @param userId unique user id
     * @return optional user
     */
    public Optional<User> getUserProfile(Long userId) {
        return userDao.findById(userId);
    }

    public UserDao getUserDao() {
        return userDao;
    }

    private List<String> validateRegistrationRequest(RegistrationRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Registration request is required.");
            return errors;
        }

        if (isBlank(request.getUsername())) {
            errors.add("Username is required.");
        }
        if (isBlank(request.getEmail())) {
            errors.add("Email is required.");
        }
        if (isBlank(request.getPassword())) {
            errors.add("Password is required.");
        }
        return errors;
    }

    private List<String> validateLoginRequest(LoginRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Login request is required.");
            return errors;
        }

        if (isBlank(request.getUsernameOrEmail())) {
            errors.add("Username or email is required.");
        }
        if (isBlank(request.getPassword())) {
            errors.add("Password is required.");
        }
        return errors;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
