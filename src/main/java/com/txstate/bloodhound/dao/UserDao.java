package com.txstate.bloodhound.dao;

import com.txstate.bloodhound.model.User;

import java.util.Optional;

/**
 * Defines persistence operations for user accounts.
 */
public interface UserDao {

    /**
     * Finds a user by unique identifier.
     *
     * @param userId user id
     * @return optional user
     */
    Optional<User> findById(Long userId);

    /**
     * Finds a user by username.
     *
     * @param username username
     * @return optional user
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email.
     *
     * @param email email
     * @return optional user
     */
    Optional<User> findByEmail(String email);

    /**
     * Persists a new user account.
     *
     * @param user user to persist
     * @return persisted user with generated identifiers when applicable
     */
    User createUser(User user);

    /**
     * Checks whether the provided username already exists.
     *
     * @param username username
     * @return true when username already exists
     */
    boolean existsByUsername(String username);

    /**
     * Checks whether the provided email already exists.
     *
     * @param email email
     * @return true when email already exists
     */
    boolean existsByEmail(String email);
}
