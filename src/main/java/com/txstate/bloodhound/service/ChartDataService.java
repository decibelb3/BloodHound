package com.txstate.bloodhound.service;

import com.txstate.bloodhound.dao.HealthMeasurementDao;
import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.model.MetricPoint;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Prepares user-scoped time-series data for JavaFX line charts.
 */
public class ChartDataService {
    private final HealthMeasurementDao healthMeasurementDao;

    public ChartDataService(HealthMeasurementDao healthMeasurementDao) {
        this.healthMeasurementDao = healthMeasurementDao;
    }

    /**
     * Returns systolic trend points for a user.
     *
     * @param userId owner user id
     * @return ascending timestamp points with non-null values
     * @throws SQLException when data retrieval fails
     */
    public List<MetricPoint> getSystolicTrend(Long userId) throws SQLException {
        return getTrend(userId, HealthMeasurement::getSystolic);
    }

    /**
     * Returns systolic trend points for a user in a date range.
     *
     * @param userId owner user id
     * @param start range start (inclusive)
     * @param end range end (inclusive)
     * @return ascending timestamp points with non-null values
     * @throws SQLException when data retrieval fails
     */
    public List<MetricPoint> getSystolicTrend(Long userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return getTrend(userId, start, end, HealthMeasurement::getSystolic);
    }

    /**
     * Returns diastolic trend points for a user.
     *
     * @param userId owner user id
     * @return ascending timestamp points with non-null values
     * @throws SQLException when data retrieval fails
     */
    public List<MetricPoint> getDiastolicTrend(Long userId) throws SQLException {
        return getTrend(userId, HealthMeasurement::getDiastolic);
    }

    /**
     * Returns diastolic trend points for a user in a date range.
     *
     * @param userId owner user id
     * @param start range start (inclusive)
     * @param end range end (inclusive)
     * @return ascending timestamp points with non-null values
     * @throws SQLException when data retrieval fails
     */
    public List<MetricPoint> getDiastolicTrend(Long userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return getTrend(userId, start, end, HealthMeasurement::getDiastolic);
    }

    /**
     * Returns total cholesterol trend points for a user.
     *
     * @param userId owner user id
     * @return ascending timestamp points with non-null values
     * @throws SQLException when data retrieval fails
     */
    public List<MetricPoint> getTotalCholesterolTrend(Long userId) throws SQLException {
        return getTrend(userId, HealthMeasurement::getTotalCholesterol);
    }

    /**
     * Returns total cholesterol trend points for a user in a date range.
     *
     * @param userId owner user id
     * @param start range start (inclusive)
     * @param end range end (inclusive)
     * @return ascending timestamp points with non-null values
     * @throws SQLException when data retrieval fails
     */
    public List<MetricPoint> getTotalCholesterolTrend(Long userId, LocalDateTime start, LocalDateTime end)
            throws SQLException {
        return getTrend(userId, start, end, HealthMeasurement::getTotalCholesterol);
    }

    /**
     * Returns HDL trend points for a user.
     *
     * @param userId owner user id
     * @return ascending timestamp points with non-null values
     * @throws SQLException when data retrieval fails
     */
    public List<MetricPoint> getHdlTrend(Long userId) throws SQLException {
        return getTrend(userId, HealthMeasurement::getHdl);
    }

    /**
     * Returns HDL trend points for a user in a date range.
     *
     * @param userId owner user id
     * @param start range start (inclusive)
     * @param end range end (inclusive)
     * @return ascending timestamp points with non-null values
     * @throws SQLException when data retrieval fails
     */
    public List<MetricPoint> getHdlTrend(Long userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return getTrend(userId, start, end, HealthMeasurement::getHdl);
    }

    /**
     * Returns LDL trend points for a user.
     *
     * @param userId owner user id
     * @return ascending timestamp points with non-null values
     * @throws SQLException when data retrieval fails
     */
    public List<MetricPoint> getLdlTrend(Long userId) throws SQLException {
        return getTrend(userId, HealthMeasurement::getLdl);
    }

    /**
     * Returns LDL trend points for a user in a date range.
     *
     * @param userId owner user id
     * @param start range start (inclusive)
     * @param end range end (inclusive)
     * @return ascending timestamp points with non-null values
     * @throws SQLException when data retrieval fails
     */
    public List<MetricPoint> getLdlTrend(Long userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return getTrend(userId, start, end, HealthMeasurement::getLdl);
    }

    /**
     * Returns weight trend points for a user.
     *
     * @param userId owner user id
     * @return ascending timestamp points with non-null values
     * @throws SQLException when data retrieval fails
     */
    public List<MetricPoint> getWeightTrend(Long userId) throws SQLException {
        return getTrend(userId, HealthMeasurement::getWeight);
    }

    /**
     * Returns weight trend points for a user in a date range.
     *
     * @param userId owner user id
     * @param start range start (inclusive)
     * @param end range end (inclusive)
     * @return ascending timestamp points with non-null values
     * @throws SQLException when data retrieval fails
     */
    public List<MetricPoint> getWeightTrend(Long userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return getTrend(userId, start, end, HealthMeasurement::getWeight);
    }

    public HealthMeasurementDao getHealthMeasurementDao() {
        return healthMeasurementDao;
    }

    private List<MetricPoint> getTrend(Long userId,
                                       Function<HealthMeasurement, Number> metricExtractor) throws SQLException {
        validateUserId(userId);
        List<HealthMeasurement> measurements = healthMeasurementDao.findByUserIdOrderedByDate(userId);
        return toMetricPoints(measurements, metricExtractor);
    }

    private List<MetricPoint> getTrend(Long userId,
                                       LocalDateTime start,
                                       LocalDateTime end,
                                       Function<HealthMeasurement, Number> metricExtractor) throws SQLException {
        validateUserId(userId);
        validateRange(start, end);
        List<HealthMeasurement> measurements = healthMeasurementDao.findByUserIdAndDateRange(userId, start, end);
        return toMetricPoints(measurements, metricExtractor);
    }

    private List<MetricPoint> toMetricPoints(List<HealthMeasurement> measurements,
                                             Function<HealthMeasurement, Number> metricExtractor) {
        return measurements.stream()
                .filter(m -> m.getMeasurementDateTime() != null)
                .map(m -> toMetricPoint(m, metricExtractor))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(MetricPoint::getTimestamp))
                .toList();
    }

    private MetricPoint toMetricPoint(HealthMeasurement measurement,
                                      Function<HealthMeasurement, Number> metricExtractor) {
        Number value = metricExtractor.apply(measurement);
        if (value == null) {
            return null;
        }
        return new MetricPoint(measurement.getMeasurementDateTime(), value.doubleValue());
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("userId must be a positive value.");
        }
    }

    private void validateRange(LocalDateTime start, LocalDateTime end) {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("end must be on/after start.");
        }
    }
}
