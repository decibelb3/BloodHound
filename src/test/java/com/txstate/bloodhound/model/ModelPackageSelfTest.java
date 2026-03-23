package com.txstate.bloodhound.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lightweight model-package regression checks that run without external test libraries.
 * <p>
 * This class is intentionally simple so the model can be validated independently of
 * controller and view layers.
 * </p>
 */
public final class ModelPackageSelfTest {
    private ModelPackageSelfTest() {
    }

    /**
     * Executes model behavior checks and exits with an exception on failure.
     *
     * @param args ignored command-line arguments
     */
    public static void main(String[] args) {
        testHealthRecordMetricPresence();
        testAddRecordResponseDefensiveCopy();
        testLipidClassificationAlertDefensiveCopy();
        testAnalyticsResultRoundTrip();
        System.out.println("ModelPackageSelfTest passed.");
    }

    private static void testHealthRecordMetricPresence() {
        HealthRecord record = new HealthRecord();
        assertFalse(record.hasAtLeastOneMetric(), "new record should have no metrics");
        record.setSystolic(120);
        assertTrue(record.hasAtLeastOneMetric(), "record should report metric presence");
    }

    private static void testAddRecordResponseDefensiveCopy() {
        HealthRecord record = new HealthRecord();
        List<String> source = new ArrayList<>(List.of("alert-one"));
        AddRecordResponse response = new AddRecordResponse(record, source);
        source.add("mutated-after-construction");
        assertEquals(1, response.getAlerts().size(), "alerts should be copied defensively");

        boolean unsupportedThrown = false;
        try {
            response.getAlerts().add("should-fail");
        } catch (UnsupportedOperationException ignored) {
            unsupportedThrown = true;
        }
        assertTrue(unsupportedThrown, "alerts view should be unmodifiable");
    }

    private static void testLipidClassificationAlertDefensiveCopy() {
        LipidClassification classification = new LipidClassification();
        List<String> alerts = new ArrayList<>(List.of("LDL high"));
        classification.setAlerts(alerts);
        alerts.add("HDL low");
        assertEquals(1, classification.getAlerts().size(), "classification should copy alert list");

        boolean unsupportedThrown = false;
        try {
            classification.getAlerts().add("should-fail");
        } catch (UnsupportedOperationException ignored) {
            unsupportedThrown = true;
        }
        assertTrue(unsupportedThrown, "classification alerts should be unmodifiable");
    }

    private static void testAnalyticsResultRoundTrip() {
        AnalyticsResult analytics = new AnalyticsResult();
        analytics.setAverageSystolic(128.4);
        analytics.setAverageDiastolic(82.3);
        analytics.setAverageHeartRate(71.2);
        analytics.setAverageTotalCholesterol(190.0);
        analytics.setAverageLdl(112.0);
        analytics.setAverageHdl(55.0);
        analytics.setAverageTriglycerides(140.0);
        analytics.setBloodPressureCategoryCounts(Map.of("Normal", 2, "Stage 1", 1));
        analytics.setAlertSummaries(List.of("No high-risk alerts detected."));
        analytics.setTrendSummary("Systolic trend is stable.");

        assertEquals(128.4, analytics.getAverageSystolic(), "systolic average should round-trip");
        assertEquals(82.3, analytics.getAverageDiastolic(), "diastolic average should round-trip");
        assertEquals(71.2, analytics.getAverageHeartRate(), "heart rate average should round-trip");
        assertEquals(190.0, analytics.getAverageTotalCholesterol(), "total cholesterol average should round-trip");
        assertEquals(112.0, analytics.getAverageLdl(), "LDL average should round-trip");
        assertEquals(55.0, analytics.getAverageHdl(), "HDL average should round-trip");
        assertEquals(140.0, analytics.getAverageTriglycerides(), "triglycerides average should round-trip");
        assertEquals(2, analytics.getBloodPressureCategoryCounts().get("Normal"), "category counts should round-trip");
        assertEquals(1, analytics.getAlertSummaries().size(), "alerts should round-trip");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        throw new IllegalStateException(message + " (expected=" + expected + ", actual=" + actual + ")");
    }
}
