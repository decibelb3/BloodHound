package com.txstate.bloodhound.service;

import com.txstate.bloodhound.dao.HealthMeasurementDao;
import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.testutil.TestDataFactory;
import com.txstate.bloodhound.util.OperationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static com.txstate.bloodhound.testutil.TestDataFactory.validMeasurement;
import static com.txstate.bloodhound.testutil.TestDataFactory.emptyMeasurement;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceTest {

    @Mock
    private HealthMeasurementDao healthMeasurementDao;

    private MeasurementService measurementService;

    @BeforeEach
    void setUp() {
        measurementService = new MeasurementService(healthMeasurementDao, new ValidationService());
    }

    @Test
    void addMeasurement_shouldAddValidMeasurement() throws SQLException {
        HealthMeasurement measurement = validMeasurement(1L, LocalDateTime.now().minusHours(1));
        when(healthMeasurementDao.save(any(HealthMeasurement.class))).thenAnswer(invocation -> {
            HealthMeasurement saved = invocation.getArgument(0);
            saved.setMeasurementId(101L);
            return saved;
        });

        OperationResult<HealthMeasurement> result = measurementService.addMeasurement(1L, measurement);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(101L, result.getData().getMeasurementId());
    }

    @Test
    void addMeasurement_shouldRejectInvalidMeasurement() {
        HealthMeasurement invalid = emptyMeasurement(1L);
        invalid.setMeasurementDateTime(LocalDateTime.now());

        OperationResult<HealthMeasurement> result = measurementService.addMeasurement(1L, invalid);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrors().contains("At least one health metric must be entered."));
    }

    @Test
    void getMeasurementHistory_shouldReturnMeasurementHistoryForUser() throws SQLException {
        List<HealthMeasurement> history = List.of(
                validMeasurement(1L, LocalDateTime.now().minusDays(2)),
                validMeasurement(1L, LocalDateTime.now().minusDays(1))
        );
        when(healthMeasurementDao.findByUserIdOrderedByDate(1L)).thenReturn(history);

        List<HealthMeasurement> result = measurementService.getMeasurementHistory(1L);

        assertEquals(2, result.size());
    }

    @Test
    void getMeasurementsByDateRange_shouldFilterByDateRange() throws SQLException {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        List<HealthMeasurement> filtered = List.of(validMeasurement(1L, LocalDateTime.now().minusDays(3)));
        when(healthMeasurementDao.findByUserIdAndDateRange(1L, start, end)).thenReturn(filtered);

        OperationResult<List<HealthMeasurement>> result = measurementService.getMeasurementsByDateRange(1L, start, end);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
    }

    @Test
    void updateMeasurement_shouldUpdateMeasurement() throws SQLException {
        HealthMeasurement existing = validMeasurement(1L, LocalDateTime.now().minusDays(1));
        existing.setMeasurementId(5L);

        HealthMeasurement updatedPayload = validMeasurement(1L, LocalDateTime.now());
        updatedPayload.setMeasurementId(5L);

        when(healthMeasurementDao.findByUserId(1L)).thenReturn(List.of(existing));
        when(healthMeasurementDao.update(any(HealthMeasurement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OperationResult<HealthMeasurement> result = measurementService.updateMeasurement(1L, updatedPayload);

        assertTrue(result.isSuccess());
        assertEquals(5L, result.getData().getMeasurementId());
    }

    @Test
    void deleteMeasurement_shouldDeleteMeasurement() throws SQLException {
        HealthMeasurement owned = validMeasurement(1L, LocalDateTime.now().minusDays(1));
        owned.setMeasurementId(11L);
        when(healthMeasurementDao.findByUserId(1L)).thenReturn(List.of(owned));
        doNothing().when(healthMeasurementDao).deleteByIdAndUserId(11L, 1L);

        OperationResult<Void> result = measurementService.deleteMeasurement(1L, 11L);

        assertTrue(result.isSuccess());
    }

    @Test
    void updateMeasurement_shouldBlockAccessToAnotherUsersMeasurement() throws SQLException {
        HealthMeasurement someoneElseMeasurement = validMeasurement(2L, LocalDateTime.now().minusDays(1));
        someoneElseMeasurement.setMeasurementId(99L);

        HealthMeasurement attempted = validMeasurement(1L, LocalDateTime.now());
        attempted.setMeasurementId(99L);

        when(healthMeasurementDao.findByUserId(1L)).thenReturn(List.of());

        OperationResult<HealthMeasurement> result = measurementService.updateMeasurement(1L, attempted);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrors().contains("Measurement not found for the authenticated user."));
    }

    @Test
    void deleteMeasurement_shouldBlockAccessToAnotherUsersMeasurement() throws SQLException {
        when(healthMeasurementDao.findByUserId(anyLong())).thenReturn(List.of());

        OperationResult<Void> result = measurementService.deleteMeasurement(1L, 22L);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrors().contains("Measurement not found for the authenticated user."));
    }
}
