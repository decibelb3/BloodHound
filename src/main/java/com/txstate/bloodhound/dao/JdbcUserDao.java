package com.txstate.bloodhound.dao;

import com.txstate.bloodhound.model.User;
import com.txstate.bloodhound.util.DatabaseConnectionManager;

import java.sql.SQLException;
import java.util.Optional;

/**
 * JDBC-based user DAO placeholder for MySQL persistence.
 */
public class JdbcUserDao implements UserDao {
    private final DatabaseConnectionManager connectionManager;

    public JdbcUserDao(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public User create(User user) throws SQLException {
        // TODO: Implement INSERT users with JDBC.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Optional<User> findById(Long id) throws SQLException {
        // TODO: Implement SELECT user by id.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Optional<User> findByUsername(String username) throws SQLException {
        // TODO: Implement SELECT user by username.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        // TODO: Implement SELECT user by email.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void update(User user) throws SQLException {
        // TODO: Implement UPDATE users.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public DatabaseConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
