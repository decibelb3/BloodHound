package com.txstate.bloodhound.testutil;

import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.model.User;

import java.time.LocalDateTime;

/**
 * Factory methods for constructing concise test fixtures.
 */
public final class TestDataFactory {
    private TestDataFactory() {
    }

    public static User user(Long id, String username, String email, String passwordHash) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        return user;
    }

    public static HealthMeasurement measurement(
            Long measurementId,
            Long userId,
            Integer systolic,
            Integer diastolic,
            Integer totalCholesterol,
            Integer hdl,
            Integer ldl,
            Double weight,
            LocalDateTime measuredAt) {
        HealthMeasurement measurement = new HealthMeasurement();
        measurement.setMeasurementId(measurementId);
        measurement.setUserId(userId);
        measurement.setSystolic(systolic);
        measurement.setDiastolic(diastolic);
        measurement.setTotalCholesterol(totalCholesterol);
        measurement.setHdl(hdl);
        measurement.setLdl(ldl);
        measurement.setWeight(weight);
        measurement.setMeasurementDateTime(measuredAt);
        measurement.setCreatedAt(measuredAt == null ? null : measuredAt.plusMinutes(1));
        return measurement;
    }

    public static HealthMeasurement validMeasurement(Long userId, LocalDateTime measuredAt) {
        return measurement(null, userId, 122, 80, 198, 52, 118, 176.5, measuredAt);
    }

    public static HealthMeasurement emptyMeasurement(Long userId) {
        return measurement(null, userId, null, null, null, null, null, null, LocalDateTime.now());
    }
}
