package com.txstate.bloodhound.dao;

import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.model.User;
import com.txstate.bloodhound.util.DatabaseConnectionManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HealthMeasurementDaoImplTest {

    private static DatabaseConnectionManager connectionManager;
    private static HealthMeasurementDaoImpl dao;

    @BeforeAll
    static void setUpSchema() throws SQLException {
        connectionManager = DaoIntegrationTestSupport.createConnectionManager();
        DaoIntegrationTestSupport.createSchema(connectionManager);
        dao = new HealthMeasurementDaoImpl(connectionManager);
    }

    @BeforeEach
    void cleanTables() throws SQLException {
        DaoIntegrationTestSupport.truncateAll(connectionManager);
    }

    @Test
    void saveAndFindByUserIdOrderedByDate_shouldPersistAndReturnDesc() throws SQLException {
        User user;
        HealthMeasurement oldMeasurement;
        HealthMeasurement recentMeasurement;
        try (Connection connection = connectionManager.getConnection()) {
            user = DaoIntegrationTestSupport.seedUser(connection, "alex", "alex@example.com", "hash");
            oldMeasurement = DaoIntegrationTestSupport.seedMeasurement(
                    connection, user.getUserId(), 130, 85, 200, 45, 130, 190.0,
                    LocalDateTime.of(2026, 1, 1, 8, 0));
            recentMeasurement = DaoIntegrationTestSupport.seedMeasurement(
                    connection, user.getUserId(), 128, 82, 198, 46, 128, 189.5,
                    LocalDateTime.of(2026, 1, 2, 8, 0));
        }

        List<HealthMeasurement> result = dao.findByUserIdOrderedByDate(user.getUserId());

        assertEquals(2, result.size());
        assertEquals(recentMeasurement.getMeasurementId(), result.get(0).getMeasurementId());
        assertEquals(oldMeasurement.getMeasurementId(), result.get(1).getMeasurementId());
    }

    @Test
    void findByUserIdAndDateRange_shouldReturnOnlyMeasurementsInRangeAsc() throws SQLException {
        User user;
        HealthMeasurement inRange1;
        HealthMeasurement inRange2;
        try (Connection connection = connectionManager.getConnection()) {
            user = DaoIntegrationTestSupport.seedUser(connection, "alex", "alex@example.com", "hash");
            DaoIntegrationTestSupport.seedMeasurement(
                    connection, user.getUserId(), 130, 85, 200, 45, 130, 190.0,
                    LocalDateTime.of(2026, 1, 1, 8, 0));
            inRange1 = DaoIntegrationTestSupport.seedMeasurement(
                    connection, user.getUserId(), 129, 84, 199, 46, 129, 189.0,
                    LocalDateTime.of(2026, 1, 2, 8, 0));
            inRange2 = DaoIntegrationTestSupport.seedMeasurement(
                    connection, user.getUserId(), 128, 83, 198, 47, 128, 188.0,
                    LocalDateTime.of(2026, 1, 3, 8, 0));
            DaoIntegrationTestSupport.seedMeasurement(
                    connection, user.getUserId(), 127, 82, 197, 48, 127, 187.0,
                    LocalDateTime.of(2026, 1, 4, 8, 0));
        }

        List<HealthMeasurement> result = dao.findByUserIdAndDateRange(
                user.getUserId(),
                LocalDateTime.of(2026, 1, 2, 0, 0),
                LocalDateTime.of(2026, 1, 3, 23, 59));

        assertEquals(2, result.size());
        assertEquals(inRange1.getMeasurementId(), result.get(0).getMeasurementId());
        assertEquals(inRange2.getMeasurementId(), result.get(1).getMeasurementId());
    }

    @Test
    void findLatestByUserId_shouldReturnNewestMeasurement() throws SQLException {
        User user;
        HealthMeasurement latest;
        try (Connection connection = connectionManager.getConnection()) {
            user = DaoIntegrationTestSupport.seedUser(connection, "alex", "alex@example.com", "hash");
            DaoIntegrationTestSupport.seedMeasurement(
                    connection, user.getUserId(), 130, 85, 200, 45, 130, 190.0,
                    LocalDateTime.of(2026, 1, 1, 8, 0));
            latest = DaoIntegrationTestSupport.seedMeasurement(
                    connection, user.getUserId(), 126, 81, 196, 49, 126, 186.5,
                    LocalDateTime.of(2026, 1, 4, 8, 0));
        }

        Optional<HealthMeasurement> result = dao.findLatestByUserId(user.getUserId());

        assertTrue(result.isPresent());
        assertEquals(latest.getMeasurementId(), result.get().getMeasurementId());
    }

    @Test
    void updateAndDeleteByIdAndUserId_shouldModifyOnlyOwnedRecords() throws SQLException {
        User owner;
        User other;
        HealthMeasurement owned;
        HealthMeasurement otherMeasurement;
        try (Connection connection = connectionManager.getConnection()) {
            owner = DaoIntegrationTestSupport.seedUser(connection, "alex", "alex@example.com", "hash");
            other = DaoIntegrationTestSupport.seedUser(connection, "jordan", "jordan@example.com", "hash");
            owned = DaoIntegrationTestSupport.seedMeasurement(
                    connection, owner.getUserId(), 130, 85, 200, 45, 130, 190.0,
                    LocalDateTime.of(2026, 1, 1, 8, 0));
            otherMeasurement = DaoIntegrationTestSupport.seedMeasurement(
                    connection, other.getUserId(), 125, 80, 190, 50, 120, 170.0,
                    LocalDateTime.of(2026, 1, 2, 8, 0));
        }

        owned.setSystolic(120);
        owned.setDiastolic(78);
        dao.update(owned);
        List<HealthMeasurement> ownerAfterUpdate = dao.findByUserId(owner.getUserId());
        assertEquals(120, ownerAfterUpdate.get(0).getSystolic());
        assertEquals(78, ownerAfterUpdate.get(0).getDiastolic());

        dao.deleteByIdAndUserId(owned.getMeasurementId(), owner.getUserId());
        List<HealthMeasurement> ownerAfterDelete = dao.findByUserId(owner.getUserId());
        assertTrue(ownerAfterDelete.isEmpty());

        List<HealthMeasurement> otherAfterDelete = dao.findByUserId(other.getUserId());
        assertEquals(1, otherAfterDelete.size());
        assertEquals(otherMeasurement.getMeasurementId(), otherAfterDelete.get(0).getMeasurementId());

        dao.deleteByIdAndUserId(otherMeasurement.getMeasurementId(), owner.getUserId());
        List<HealthMeasurement> otherStillPresent = dao.findByUserId(other.getUserId());
        assertFalse(otherStillPresent.isEmpty());
    }
}
