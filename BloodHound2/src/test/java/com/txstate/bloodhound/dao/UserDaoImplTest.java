package com.txstate.bloodhound.dao;

import com.txstate.bloodhound.model.User;
import com.txstate.bloodhound.util.DatabaseConnectionManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoImplTest {
    private static DatabaseConnectionManager connectionManager;
    private static UserDaoImpl userDao;

    @BeforeAll
    static void setupSchema() throws SQLException {
        connectionManager = DaoIntegrationTestSupport.createConnectionManager();
        DaoIntegrationTestSupport.createSchema(connectionManager);
        userDao = new UserDaoImpl(connectionManager);
    }

    @BeforeEach
    void clearTables() throws SQLException {
        DaoIntegrationTestSupport.truncateAll(connectionManager);
    }

    @Test
    void createUserAndFindByUsernameShouldPersistAndLoadUser() {
        User user = new User();
        user.setUsername("dao.user");
        user.setEmail("dao.user@test.local");
        user.setPasswordHash("hash-value");
        user.setCreatedAt(LocalDateTime.of(2026, 3, 1, 10, 0));

        User created = userDao.createUser(user);

        assertNotNull(created.getUserId());
        Optional<User> loaded = userDao.findByUsername("dao.user");
        assertTrue(loaded.isPresent());
        assertEquals("dao.user@test.local", loaded.get().getEmail());
        assertEquals("hash-value", loaded.get().getPasswordHash());
    }

    @Test
    void existsByUsernameAndExistsByEmailShouldReturnTrueWhenRecordsExist() {
        try (Connection connection = connectionManager.getConnection()) {
            DaoIntegrationTestSupport.seedUser(connection, "exists.user", "exists@test.local", "hash");
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

        assertTrue(userDao.existsByUsername("exists.user"));
        assertTrue(userDao.existsByEmail("exists@test.local"));
        assertFalse(userDao.existsByUsername("missing.user"));
        assertFalse(userDao.existsByEmail("missing@test.local"));
    }

    @Test
    void findByIdShouldReturnEmptyWhenUserMissing() {
        Optional<User> result = userDao.findById(9999L);
        assertTrue(result.isEmpty());
    }
}
