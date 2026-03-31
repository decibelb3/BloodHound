package com.txstate.bloodhound.service;

import com.txstate.bloodhound.dao.HealthMeasurementDao;
import com.txstate.bloodhound.model.HealthMeasurement;

import java.sql.SQLException;
import java.util.List;

/**
 * Coordinates analytical summaries over user-owned measurement histories.
 */
public class AnalyticsService {
    private final HealthMeasurementDao healthMeasurementDao;
    private final ChartDataService chartDataService;

    public AnalyticsService(HealthMeasurementDao healthMeasurementDao, ChartDataService chartDataService) {
        this.healthMeasurementDao = healthMeasurementDao;
        this.chartDataService = chartDataService;
    }

    /**
     * Computes average systolic blood pressure.
     *
     * @param measurements user measurement history
     * @return average value placeholder
     */
    public double calculateAverageSystolic(List<HealthMeasurement> measurements) {
        // TODO: Implement analytics calculation.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Computes average diastolic blood pressure.
     *
     * @param measurements user measurement history
     * @return average value placeholder
     */
    public double calculateAverageDiastolic(List<HealthMeasurement> measurements) {
        // TODO: Implement analytics calculation.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Computes average total cholesterol.
     *
     * @param measurements user measurement history
     * @return average value placeholder
     */
    public double calculateAverageTotalCholesterol(List<HealthMeasurement> measurements) {
        // TODO: Implement analytics calculation.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Loads all measurements for a user and computes summary analytics.
     *
     * @param userId owner user id
     * @return placeholder summary text
     * @throws SQLException when data retrieval fails
     */
    public String buildSummaryForUser(Long userId) throws SQLException {
        // TODO: Fetch user measurements and compute high-level summary output.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public HealthMeasurementDao getHealthMeasurementDao() {
        return healthMeasurementDao;
    }

    public ChartDataService getChartDataService() {
        return chartDataService;
    }
}
