package com.txstate.bloodhound.util;

import com.txstate.bloodhound.dao.HealthMeasurementDao;
import com.txstate.bloodhound.dao.UserDao;
import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Seeds demo users and health measurements for local testing.
 * <p>
 * This initializer is intentionally separate from services and business logic.
 * It uses DAO-level persistence and is idempotent:
 * existing users are reused and measurements with the same timestamp are not duplicated.
 */
public class DemoDataInitializer {
    private final UserDao userDao;
    private final HealthMeasurementDao healthMeasurementDao;

    /**
     * Creates a demo-data initializer.
     *
     * @param userDao user persistence dependency
     * @param healthMeasurementDao measurement persistence dependency
     */
    public DemoDataInitializer(UserDao userDao, HealthMeasurementDao healthMeasurementDao) {
        this.userDao = Objects.requireNonNull(userDao, "userDao must not be null");
        this.healthMeasurementDao = Objects.requireNonNull(healthMeasurementDao, "healthMeasurementDao must not be null");
    }

    /**
     * Seeds two sample users and measurements if missing.
     */
    public void seedIfNeeded() {
        try {
            seedUser(SampleUser.alex(), sampleMeasurementsAlex());
            seedUser(SampleUser.jordan(), sampleMeasurementsJordan());
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to seed demo data: " + exception.getMessage(), exception);
        }
    }

    private void seedUser(SampleUser sampleUser, List<SampleMeasurement> samples) throws SQLException {
        User user = findOrCreateUser(sampleUser);
        if (user.getUserId() == null) {
            return;
        }

        Set<LocalDateTime> existingTimestamps = new HashSet<>();
        for (HealthMeasurement existing : healthMeasurementDao.findByUserId(user.getUserId())) {
            if (existing.getMeasurementDateTime() != null) {
                existingTimestamps.add(existing.getMeasurementDateTime());
            }
        }

        for (SampleMeasurement sample : samples) {
            if (existingTimestamps.contains(sample.measuredAt())) {
                continue;
            }
            HealthMeasurement measurement = new HealthMeasurement();
            measurement.setUserId(user.getUserId());
            measurement.setSystolic(sample.systolic());
            measurement.setDiastolic(sample.diastolic());
            measurement.setTotalCholesterol(sample.totalCholesterol());
            measurement.setHdl(sample.hdl());
            measurement.setLdl(sample.ldl());
            measurement.setWeight(sample.weight());
            measurement.setMeasurementDateTime(sample.measuredAt());
            measurement.setCreatedAt(sample.measuredAt().plusMinutes(2));
            healthMeasurementDao.save(measurement);
            existingTimestamps.add(sample.measuredAt());
        }
    }

    private User findOrCreateUser(SampleUser sampleUser) {
        Optional<User> byUsername = userDao.findByUsername(sampleUser.username());
        if (byUsername.isPresent()) {
            return byUsername.get();
        }

        Optional<User> byEmail = userDao.findByEmail(sampleUser.email());
        if (byEmail.isPresent()) {
            return byEmail.get();
        }

        User user = new User();
        user.setUsername(sampleUser.username());
        user.setEmail(sampleUser.email());
        user.setPasswordHash(PasswordUtil.hashPassword(sampleUser.password()));
        user.setCreatedAt(LocalDateTime.now());
        return userDao.createUser(user);
    }

    private List<SampleMeasurement> sampleMeasurementsAlex() {
        return List.of(
                new SampleMeasurement(LocalDateTime.of(2025, 11, 4, 7, 30), 142, 92, 225, 42, 153, 201.8),
                new SampleMeasurement(LocalDateTime.of(2025, 11, 18, 7, 40), 139, 89, 218, 43, 149, 199.6),
                new SampleMeasurement(LocalDateTime.of(2025, 12, 2, 7, 25), 136, 88, 212, 44, 145, 198.2),
                new SampleMeasurement(LocalDateTime.of(2025, 12, 16, 7, 35), 134, 86, 206, 46, 141, 196.4),
                new SampleMeasurement(LocalDateTime.of(2026, 1, 6, 7, 20), 131, 84, 201, 48, 137, 194.1),
                new SampleMeasurement(LocalDateTime.of(2026, 1, 24, 8, 5), 129, 83, 198, 49, 134, 192.7),
                new SampleMeasurement(LocalDateTime.of(2026, 2, 11, 7, 50), 127, 81, 194, 51, 130, 191.3),
                new SampleMeasurement(LocalDateTime.of(2026, 3, 2, 7, 45), 125, 80, 190, 53, 126, 189.9)
        );
    }

    private List<SampleMeasurement> sampleMeasurementsJordan() {
        return List.of(
                new SampleMeasurement(LocalDateTime.of(2025, 11, 3, 18, 15), 128, 82, 208, 55, 127, 168.2),
                new SampleMeasurement(LocalDateTime.of(2025, 11, 17, 18, 10), 126, 80, 204, 56, 124, 167.4),
                new SampleMeasurement(LocalDateTime.of(2025, 12, 1, 18, 20), 125, 79, 201, 57, 121, 166.1),
                new SampleMeasurement(LocalDateTime.of(2025, 12, 15, 18, 5), 124, 78, 197, 58, 118, 165.6),
                new SampleMeasurement(LocalDateTime.of(2026, 1, 5, 18, 25), 123, 78, 194, 60, 114, 164.9),
                new SampleMeasurement(LocalDateTime.of(2026, 1, 23, 18, 0), 122, 77, 191, 61, 111, 164.4),
                new SampleMeasurement(LocalDateTime.of(2026, 2, 10, 18, 30), 121, 76, 188, 62, 108, 163.9),
                new SampleMeasurement(LocalDateTime.of(2026, 3, 1, 18, 10), 120, 76, 186, 63, 106, 163.2)
        );
    }

    private record SampleUser(String username, String email, String password) {
        private static SampleUser alex() {
            return new SampleUser("demo.alex", "demo.alex@bloodhound.local", "DemoPass123!");
        }

        private static SampleUser jordan() {
            return new SampleUser("demo.jordan", "demo.jordan@bloodhound.local", "DemoPass123!");
        }
    }

    private record SampleMeasurement(
            LocalDateTime measuredAt,
            Integer systolic,
            Integer diastolic,
            Integer totalCholesterol,
            Integer hdl,
            Integer ldl,
            Double weight) {
    }
}
