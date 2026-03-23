package com.txstate.bloodhound.service;

import com.txstate.bloodhound.model.AnalyticsResult;
import com.txstate.bloodhound.model.HealthRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Computes aggregate metrics, category counts, and alert summaries.
 */
public class AnalyticsEngine {

    /**
     * Computes aggregate analytics for the provided records.
     *
     * @param records source records
     * @return analytics result containing averages, counts, alerts, and trend summary
     */
    public AnalyticsResult computeAnalytics(List<HealthRecord> records) {
        AnalyticsResult result = new AnalyticsResult();
        if (records == null || records.isEmpty()) {
            result.setTrendSummary("No records available for trend analysis.");
            return result;
        }

        result.setAverageSystolic(average(records, HealthRecord::getSystolic));
        result.setAverageDiastolic(average(records, HealthRecord::getDiastolic));
        result.setAverageHeartRate(average(records, HealthRecord::getHeartRate));
        result.setAverageTotalCholesterol(average(records, HealthRecord::getTotalCholesterol));
        result.setAverageLdl(average(records, HealthRecord::getLdl));
        result.setAverageHdl(average(records, HealthRecord::getHdl));
        result.setAverageTriglycerides(average(records, HealthRecord::getTriglycerides));
        result.setBloodPressureCategoryCounts(categoryCounts(records));
        result.setAlertSummaries(buildAlertSummary(records));
        result.setTrendSummary(buildTrendSummary(records));
        return result;
    }

    private Double average(List<HealthRecord> records, java.util.function.Function<HealthRecord, Integer> extractor) {
        List<Integer> values = records.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (values.isEmpty()) {
            return null;
        }
        return values.stream().mapToInt(Integer::intValue).average().orElse(Double.NaN);
    }

    private Map<String, Integer> categoryCounts(List<HealthRecord> records) {
        Map<String, Integer> counts = new HashMap<>();
        for (HealthRecord record : records) {
            String category = record.getBloodPressureCategory();
            if (category == null || category.isBlank()) {
                category = "Unclassified";
            }
            counts.put(category, counts.getOrDefault(category, 0) + 1);
        }
        return counts;
    }

    private List<String> buildAlertSummary(List<HealthRecord> records) {
        int bpAlertCount = 0;
        int lipidAlertCount = 0;

        for (HealthRecord record : records) {
            if ("Stage 2".equals(record.getBloodPressureCategory()) || "Crisis".equals(record.getBloodPressureCategory())) {
                bpAlertCount++;
            }
            if ("Lipid risk alerts present.".equals(record.getLipidSummary())) {
                lipidAlertCount++;
            }
        }

        List<String> alerts = new ArrayList<>();
        if (bpAlertCount > 0) {
            alerts.add("High-risk blood pressure records: " + bpAlertCount);
        }
        if (lipidAlertCount > 0) {
            alerts.add("Lipid risk records: " + lipidAlertCount);
        }
        if (alerts.isEmpty()) {
            alerts.add("No high-risk alerts detected.");
        }
        return alerts;
    }

    private String buildTrendSummary(List<HealthRecord> records) {
        List<HealthRecord> sorted = new ArrayList<>(records);
        sorted.sort(Comparator.comparingLong(HealthRecord::getTimestampEpochMillis).reversed());

        List<Integer> recent = new ArrayList<>();
        List<Integer> prior = new ArrayList<>();

        for (int i = 0; i < sorted.size(); i++) {
            Integer systolic = sorted.get(i).getSystolic();
            if (systolic == null) {
                continue;
            }
            if (recent.size() < 5) {
                recent.add(systolic);
            } else if (prior.size() < 5) {
                prior.add(systolic);
            }
            if (prior.size() >= 5) {
                break;
            }
        }

        if (recent.isEmpty()) {
            return "Insufficient BP data for trend analysis.";
        }

        double recentAvg = recent.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        if (prior.isEmpty()) {
            return "Recent average systolic: " + Math.round(recentAvg * 10.0) / 10.0;
        }

        double priorAvg = prior.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        if (recentAvg > priorAvg + 2.0) {
            return "Systolic trend is rising (recent avg " + round1(recentAvg)
                    + " vs prior " + round1(priorAvg) + ").";
        }
        if (recentAvg < priorAvg - 2.0) {
            return "Systolic trend is improving (recent avg " + round1(recentAvg)
                    + " vs prior " + round1(priorAvg) + ").";
        }
        return "Systolic trend is stable (recent avg " + round1(recentAvg)
                + " vs prior " + round1(priorAvg) + ").";
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
