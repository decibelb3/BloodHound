package com.txstate.bloodhound.service;

import com.txstate.bloodhound.dao.HealthMeasurementDao;
import com.txstate.bloodhound.model.DashboardSummary;
import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static com.txstate.bloodhound.testutil.TestDataFactory.measurement;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnalyticsServiceTest {

    @Test
    void computeAveragesCorrectly() throws SQLException {
        HealthMeasurementDao dao = mock(HealthMeasurementDao.class);
        ChartDataService chartDataService = mock(ChartDataService.class);
        AnalyticsService service = new AnalyticsService(dao, chartDataService);

        Long userId = 7L;
        HealthMeasurement m1 = measurement(1L, userId, 120, 80, 200, 50, 120, 180.0, LocalDateTime.of(2026, 1, 1, 8, 0));
        HealthMeasurement m2 = measurement(2L, userId, 140, 90, 220, 60, 140, 190.0, LocalDateTime.of(2026, 1, 2, 8, 0));
        when(dao.findByUserId(userId)).thenReturn(List.of(m1, m2));

        DashboardSummary summary = service.getDashboardSummary(userId);

        assertEquals(130.0, summary.getAverageSystolic());
        assertEquals(85.0, summary.getAverageDiastolic());
        assertEquals(210.0, summary.getAverageTotalCholesterol());
        assertEquals(55.0, summary.getAverageHdl());
        assertEquals(130.0, summary.getAverageLdl());
        assertEquals(185.0, summary.getAverageWeight());
    }

    @Test
    void ignoreNullValues() throws SQLException {
        HealthMeasurementDao dao = mock(HealthMeasurementDao.class);
        ChartDataService chartDataService = mock(ChartDataService.class);
        AnalyticsService service = new AnalyticsService(dao, chartDataService);

        Long userId = 7L;
        HealthMeasurement withValues = measurement(1L, userId, 130, null, 210, null, 130, null, LocalDateTime.of(2026, 1, 1, 8, 0));
        HealthMeasurement mostlyNull = measurement(2L, userId, null, null, null, 52, null, 178.5, LocalDateTime.of(2026, 1, 2, 8, 0));
        when(dao.findByUserId(userId)).thenReturn(List.of(withValues, mostlyNull));

        DashboardSummary summary = service.getDashboardSummary(userId);

        assertEquals(130.0, summary.getAverageSystolic());
        assertNull(summary.getAverageDiastolic());
        assertEquals(210.0, summary.getAverageTotalCholesterol());
        assertEquals(52.0, summary.getAverageHdl());
        assertEquals(130.0, summary.getAverageLdl());
        assertEquals(178.5, summary.getAverageWeight());
    }

    @Test
    void getLatestValuesCorrectly() throws SQLException {
        HealthMeasurementDao dao = mock(HealthMeasurementDao.class);
        ChartDataService chartDataService = mock(ChartDataService.class);
        AnalyticsService service = new AnalyticsService(dao, chartDataService);

        Long userId = 7L;
        HealthMeasurement older = measurement(1L, userId, 124, 82, 206, 53, 129, 176.4, LocalDateTime.of(2026, 1, 1, 8, 0));
        HealthMeasurement latest = measurement(2L, userId, 121, 79, 200, 57, 122, 174.9, LocalDateTime.of(2026, 1, 3, 8, 0));
        when(dao.findByUserId(userId)).thenReturn(List.of(older, latest));

        DashboardSummary summary = service.getDashboardSummary(userId);

        assertEquals(121, summary.getLatestSystolic());
        assertEquals(79, summary.getLatestDiastolic());
        assertEquals(200, summary.getLatestTotalCholesterol());
        assertEquals(57, summary.getLatestHdl());
        assertEquals(122, summary.getLatestLdl());
        assertEquals(174.9, summary.getLatestWeight());
    }

    @Test
    void handleEmptyDataset() throws SQLException {
        HealthMeasurementDao dao = mock(HealthMeasurementDao.class);
        ChartDataService chartDataService = mock(ChartDataService.class);
        AnalyticsService service = new AnalyticsService(dao, chartDataService);

        Long userId = 7L;
        when(dao.findByUserId(userId)).thenReturn(List.of());

        DashboardSummary summary = service.getDashboardSummary(userId);

        assertNotNull(summary);
        assertNull(summary.getLatestSystolic());
        assertNull(summary.getAverageSystolic());
        assertNull(summary.getAverageWeight());
    }
}
