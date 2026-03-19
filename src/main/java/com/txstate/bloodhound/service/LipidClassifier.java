package com.txstate.bloodhound.service;

import com.txstate.bloodhound.model.HealthRecord;
import com.txstate.bloodhound.model.LipidClassification;

import java.util.ArrayList;
import java.util.List;

/**
 * Classifies lipid panel values and generates summary alerts.
 */
public class LipidClassifier {

    public LipidClassification classify(HealthRecord record) {
        LipidClassification classification = new LipidClassification();
        List<String> alerts = new ArrayList<>();

        classification.setTotalCholesterolCategory(classifyTotalCholesterol(record.getTotalCholesterol()));
        classification.setLdlCategory(classifyLdl(record.getLdl()));
        classification.setHdlCategory(classifyHdl(record.getHdl()));
        classification.setTriglyceridesCategory(classifyTriglycerides(record.getTriglycerides()));

        if ("High".equals(classification.getTotalCholesterolCategory())) {
            alerts.add("Total cholesterol is in a high-risk range.");
        }
        if ("High".equals(classification.getLdlCategory()) || "Very High".equals(classification.getLdlCategory())) {
            alerts.add("LDL is elevated.");
        }
        if ("Low".equals(classification.getHdlCategory())) {
            alerts.add("HDL is low.");
        }
        if ("High".equals(classification.getTriglyceridesCategory())
                || "Very High".equals(classification.getTriglyceridesCategory())) {
            alerts.add("Triglycerides are elevated.");
        }

        classification.setSummary(alerts.isEmpty() ? "No lipid risk alerts." : "Lipid risk alerts present.");
        classification.setAlerts(alerts);
        return classification;
    }

    public String classifyTotalCholesterol(Integer value) {
        if (value == null) {
            return "Not Provided";
        }
        if (value < 200) {
            return "Desirable";
        }
        if (value < 240) {
            return "Borderline High";
        }
        return "High";
    }

    public String classifyLdl(Integer value) {
        if (value == null) {
            return "Not Provided";
        }
        if (value < 100) {
            return "Optimal";
        }
        if (value < 130) {
            return "Near Optimal";
        }
        if (value < 160) {
            return "Borderline High";
        }
        if (value < 190) {
            return "High";
        }
        return "Very High";
    }

    public String classifyHdl(Integer value) {
        if (value == null) {
            return "Not Provided";
        }
        if (value < 40) {
            return "Low";
        }
        if (value < 60) {
            return "Normal";
        }
        return "Protective";
    }

    public String classifyTriglycerides(Integer value) {
        if (value == null) {
            return "Not Provided";
        }
        if (value < 150) {
            return "Normal";
        }
        if (value < 200) {
            return "Borderline High";
        }
        if (value < 500) {
            return "High";
        }
        return "Very High";
    }
}
