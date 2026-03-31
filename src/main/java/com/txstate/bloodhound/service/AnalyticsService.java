package com.txstate.bloodhound.service;

import com.txstate.bloodhound.dao.HealthMeasurementDao;
import com.txstate.bloodhound.model.DashboardSummary;
import com.txstate.bloodhound.model.HealthMeasurement;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
     * Computes a full dashboard summary for a user using all available measurements.
     *
     * @param userId owner user identifier
     * @return dashboard summary for latest and average values
     * @throws SQLException when data retrieval fails
     */
    public DashboardSummary getDashboardSummary(Long userId) throws SQLException {
        validateUserId(userId);
        List<HealthMeasurement> measurements = healthMeasurementDao.findByUserId(userId);
        return buildSummary(measurements);
    }

    /**
     * Computes a dashboard summary for a user within a date/time range.
     *
     * @param userId owner user identifier
     * @param start range start (inclusive)
     * @param end range end (inclusive)
     * @return range-based dashboard summary
     * @throws SQLException when data retrieval fails
     */
    public DashboardSummary getDashboardSummaryForDateRange(Long userId,
                                                            LocalDateTime start,
                                                            LocalDateTime end) throws SQLException {
        validateUserId(userId);
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("end must be on/after start.");
        }

        List<HealthMeasurement> measurements = healthMeasurementDao.findByUserIdAndDateRange(userId, start, end);
        return buildSummary(measurements);
    }

    /**
     * Loads all measurements for a user and computes summary analytics.
     *
     * @param userId owner user id
     * @return summary object string representation
     * @throws SQLException when data retrieval fails
     */
    public String buildSummaryForUser(Long userId) throws SQLException {
        return getDashboardSummary(userId).toString();
    }

    private DashboardSummary buildSummary(List<HealthMeasurement> measurements) {
        DashboardSummary summary = new DashboardSummary();
        if (measurements == null || measurements.isEmpty()) {
            return summary;
        }

        HealthMeasurement latest = selectLatest(measurements);
        summary.setLatestSystolic(latest.getSystolic());
        summary.setLatestDiastolic(latest.getDiastolic());
        summary.setLatestTotalCholesterol(latest.getTotalCholesterol());
        summary.setLatestHdl(latest.getHdl());
        summary.setLatestLdl(latest.getLdl());
        summary.setLatestWeight(latest.getWeight());

        summary.setAverageSystolic(averageInteger(measurements, HealthMeasurement::getSystolic));
        summary.setAverageDiastolic(averageInteger(measurements, HealthMeasurement::getDiastolic));
        summary.setAverageTotalCholesterol(averageInteger(measurements, HealthMeasurement::getTotalCholesterol));
        summary.setAverageHdl(averageInteger(measurements, HealthMeasurement::getHdl));
        summary.setAverageLdl(averageInteger(measurements, HealthMeasurement::getLdl));
        summary.setAverageWeight(averageDouble(measurements, HealthMeasurement::getWeight));
        return summary;
    }

    private HealthMeasurement selectLatest(List<HealthMeasurement> measurements) {
        return measurements.stream()
                .filter(m -> m.getMeasurementDateTime() != null)
                .max(Comparator.comparing(HealthMeasurement::getMeasurementDateTime))
                .orElseGet(() -> measurements.get(measurements.size() - 1));
    }

    private Double averageInteger(List<HealthMeasurement> measurements,
                                  Function<HealthMeasurement, Integer> extractor) {
        return measurements.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toList(), values -> {
                    if (values.isEmpty()) {
                        return null;
                    }
                    return values.stream().mapToInt(Integer::intValue).average().orElse(Double.NaN);
                }));
    }

    private Double averageDouble(List<HealthMeasurement> measurements,
                                 Function<HealthMeasurement, Double> extractor) {
        return measurements.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toList(), values -> {
                    if (values.isEmpty()) {
                        return null;
                    }
                    return values.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
                }));
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("userId must be a positive value.");
        }
    }

    public HealthMeasurementDao getHealthMeasurementDao() {
        return healthMeasurementDao;
    }

    public ChartDataService getChartDataService() {
        return chartDataService;
    }
}
