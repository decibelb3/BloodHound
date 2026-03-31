package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.DashboardSummary;
import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.model.MetricPoint;
import com.txstate.bloodhound.util.OperationResult;
import com.txstate.bloodhound.service.AnalyticsService;
import com.txstate.bloodhound.service.ChartDataService;
import com.txstate.bloodhound.service.MeasurementService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Placeholder controller for the post-login dashboard and measurement management.
 */
public class DashboardViewController {
    private final AppState appState;
    private final MeasurementService measurementService;
    private final AnalyticsService analyticsService;
    private final ChartDataService chartDataService;

    public DashboardViewController(AppState appState,
                                   MeasurementService measurementService,
                                   AnalyticsService analyticsService,
                                   ChartDataService chartDataService) {
        this.appState = appState;
        this.measurementService = measurementService;
        this.analyticsService = analyticsService;
        this.chartDataService = chartDataService;
    }

    /**
     * Loads dashboard summary for the currently authenticated user.
     *
     * @return operation result with dashboard summary
     */
    public OperationResult<DashboardSummary> loadDashboardSummary() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return OperationResult.failure("Unable to load dashboard.", List.of("No authenticated user."));
        }
        try {
            DashboardSummary summary = analyticsService.getDashboardSummary(userId);
            return OperationResult.success("Dashboard summary loaded.", summary);
        } catch (Exception exception) {
            return OperationResult.failure("Unable to load dashboard summary.", List.of(exception.getMessage()));
        }
    }

    /**
     * Loads dashboard summary for the currently authenticated user in a date range.
     *
     * @param startInclusive start timestamp
     * @param endInclusive end timestamp
     * @return operation result with range-based summary
     */
    public OperationResult<DashboardSummary> loadDashboardSummary(LocalDateTime startInclusive,
                                                                  LocalDateTime endInclusive) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return OperationResult.failure("Unable to load dashboard.", List.of("No authenticated user."));
        }
        try {
            DashboardSummary summary = analyticsService.getDashboardSummaryForDateRange(userId, startInclusive, endInclusive);
            return OperationResult.success("Range dashboard summary loaded.", summary);
        } catch (Exception exception) {
            return OperationResult.failure("Unable to load range dashboard summary.", List.of(exception.getMessage()));
        }
    }

    public List<HealthMeasurement> loadCurrentUserMeasurements() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return List.of();
        }
        return measurementService.getMeasurementHistory(userId);
    }

    public OperationResult<List<HealthMeasurement>> loadCurrentUserMeasurements(LocalDateTime startInclusive,
                                                                                LocalDateTime endInclusive) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return OperationResult.failure("Unable to load measurements.", List.of("No authenticated user."));
        }
        return measurementService.getMeasurementsByDateRange(userId, startInclusive, endInclusive);
    }

    /**
     * Adds a measurement for the currently authenticated user.
     *
     * @param measurement measurement payload
     * @return operation result with persisted measurement
     */
    public OperationResult<HealthMeasurement> addMeasurement(HealthMeasurement measurement) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return OperationResult.failure("Unable to add measurement.", List.of("No authenticated user."));
        }
        return measurementService.addMeasurement(userId, measurement);
    }

    public HealthMeasurement createMeasurement(HealthMeasurement measurement) {
        OperationResult<HealthMeasurement> result = addMeasurement(measurement);
        if (!result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

    public HealthMeasurement updateMeasurement(HealthMeasurement measurement) {
        OperationResult<HealthMeasurement> result = updateMeasurementResult(measurement);
        if (!result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

    /**
     * Updates a measurement for the currently authenticated user.
     *
     * @param measurement updated measurement payload
     * @return operation result with updated measurement
     */
    public OperationResult<HealthMeasurement> updateMeasurementResult(HealthMeasurement measurement) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return OperationResult.failure("Unable to update measurement.", List.of("No authenticated user."));
        }
        return measurementService.updateMeasurement(userId, measurement);
    }

    public boolean deleteMeasurement(Long measurementId) {
        OperationResult<Void> result = deleteMeasurementResult(measurementId);
        return result.isSuccess();
    }

    /**
     * Deletes a measurement for the currently authenticated user.
     *
     * @param measurementId measurement id to delete
     * @return operation result describing delete outcome
     */
    public OperationResult<Void> deleteMeasurementResult(Long measurementId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return OperationResult.failure("Unable to delete measurement.", List.of("No authenticated user."));
        }
        return measurementService.deleteMeasurement(userId, measurementId);
    }

    public List<MetricPoint> loadTrendPoints(String metricKey,
                                            LocalDateTime startInclusive,
                                            LocalDateTime endInclusive) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return List.of();
        }
        try {
            if ("systolic".equalsIgnoreCase(metricKey)) {
                return startInclusive == null || endInclusive == null
                        ? chartDataService.getSystolicTrend(userId)
                        : chartDataService.getSystolicTrend(userId, startInclusive, endInclusive);
            }
            if ("diastolic".equalsIgnoreCase(metricKey)) {
                return startInclusive == null || endInclusive == null
                        ? chartDataService.getDiastolicTrend(userId)
                        : chartDataService.getDiastolicTrend(userId, startInclusive, endInclusive);
            }
            if ("totalCholesterol".equalsIgnoreCase(metricKey) || "cholesterol".equalsIgnoreCase(metricKey)) {
                return startInclusive == null || endInclusive == null
                        ? chartDataService.getTotalCholesterolTrend(userId)
                        : chartDataService.getTotalCholesterolTrend(userId, startInclusive, endInclusive);
            }
            if ("hdl".equalsIgnoreCase(metricKey)) {
                return startInclusive == null || endInclusive == null
                        ? chartDataService.getHdlTrend(userId)
                        : chartDataService.getHdlTrend(userId, startInclusive, endInclusive);
            }
            if ("ldl".equalsIgnoreCase(metricKey)) {
                return startInclusive == null || endInclusive == null
                        ? chartDataService.getLdlTrend(userId)
                        : chartDataService.getLdlTrend(userId, startInclusive, endInclusive);
            }
            if ("weight".equalsIgnoreCase(metricKey)) {
                return startInclusive == null || endInclusive == null
                        ? chartDataService.getWeightTrend(userId)
                        : chartDataService.getWeightTrend(userId, startInclusive, endInclusive);
            }
            return List.of();
        } catch (Exception exception) {
            return List.of();
        }
    }

    public MeasurementService getMeasurementService() {
        return measurementService;
    }

    public ChartDataService getChartDataService() {
        return chartDataService;
    }

    public AnalyticsService getAnalyticsService() {
        return analyticsService;
    }

    public AppState getAppState() {
        return appState;
    }

    private Long getCurrentUserId() {
        if (appState.getCurrentUser() == null) {
            return null;
        }
        return appState.getCurrentUser().getUserId();
    }
}
