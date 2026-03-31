package com.txstate.bloodhound.service;

import com.txstate.bloodhound.dao.UserDao;
import com.txstate.bloodhound.model.User;

import java.sql.SQLException;
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
     * Registers a new account.
     *
     * @param username unique username
     * @param email unique email
     * @param plainPassword raw password input
     * @return created user
     * @throws SQLException when persistence fails
     */
    public User register(String username, String email, String plainPassword) throws SQLException {
        // TODO: Validate inputs, hash password, enforce uniqueness, persist user.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Authenticates a user by username and password.
     *
     * @param username username
     * @param plainPassword raw password input
     * @return optional authenticated user
     * @throws SQLException when lookup fails
     */
    public Optional<User> login(String username, String plainPassword) throws SQLException {
        // TODO: Fetch user and verify password hash.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Looks up user profile details.
     *
     * @param userId unique user id
     * @return optional user
     * @throws SQLException when query fails
     */
    public Optional<User> getUserProfile(Long userId) throws SQLException {
        // TODO: Delegate to user DAO.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public UserDao getUserDao() {
        return userDao;
    }
}
