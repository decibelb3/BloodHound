package com.txstate.bloodhound.service;

/**
 * Classifies blood pressure based on AHA-style threshold logic.
 */
public class BloodPressureClassifier {

    public String classifyBloodPressure(Integer systolic, Integer diastolic) {
        if (systolic == null && diastolic == null) {
            return "Not Provided";
        }
        if (isAtLeast(systolic, 181) || isAtLeast(diastolic, 121)) {
            return "Crisis";
        }
        if (isAtLeast(systolic, 140) || isAtLeast(diastolic, 90)) {
            return "Stage 2";
        }
        if (isAtLeast(systolic, 130) || isAtLeast(diastolic, 80)) {
            return "Stage 1";
        }
        if (inRange(systolic, 120, 129) && isBelow(diastolic, 80)) {
            return "Elevated";
        }
        if (isBelow(systolic, 120) && isBelow(diastolic, 80)) {
            return "Normal";
        }
        return "Unclassified";
    }

    public boolean isHighRisk(String category) {
        return "Stage 2".equals(category) || "Crisis".equals(category);
    }

    private boolean isAtLeast(Integer value, int threshold) {
        return value != null && value >= threshold;
    }

    private boolean inRange(Integer value, int min, int max) {
        return value != null && value >= min && value <= max;
    }

    private boolean isBelow(Integer value, int threshold) {
        return value == null || value < threshold;
    }
}
