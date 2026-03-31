package com.txstate.bloodhound.dao;

import com.txstate.bloodhound.model.User;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Defines persistence operations for user accounts.
 */
public interface UserDao {

    /**
     * Persists a new user account.
     *
     * @param user user to persist
     * @return persisted user with generated identifiers when applicable
     * @throws SQLException when persistence fails
     */
    User create(User user) throws SQLException;

    /**
     * Finds a user by unique identifier.
     *
     * @param id user id
     * @return optional user
     * @throws SQLException when query fails
     */
    Optional<User> findById(Long id) throws SQLException;

    /**
     * Finds a user by username.
     *
     * @param username username
     * @return optional user
     * @throws SQLException when query fails
     */
    Optional<User> findByUsername(String username) throws SQLException;

    /**
     * Finds a user by email.
     *
     * @param email email
     * @return optional user
     * @throws SQLException when query fails
     */
    Optional<User> findByEmail(String email) throws SQLException;

    /**
     * Updates persisted fields for a user.
     *
     * @param user user data to update
     * @throws SQLException when update fails
     */
    void update(User user) throws SQLException;
}
