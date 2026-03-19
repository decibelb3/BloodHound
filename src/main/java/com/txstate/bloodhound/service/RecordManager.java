package com.txstate.bloodhound.service;

import com.txstate.bloodhound.model.AddRecordResponse;
import com.txstate.bloodhound.model.AnalyticsResult;
import com.txstate.bloodhound.model.HealthRecord;
import com.txstate.bloodhound.model.LipidClassification;
import com.txstate.bloodhound.storage.FileStorageService;
import com.txstate.bloodhound.util.DateTimeUtil;
import com.txstate.bloodhound.util.OperationResult;
import com.txstate.bloodhound.util.StorageInitializationResult;
import com.txstate.bloodhound.util.ValidationResult;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Main service façade coordinating validation, classification, persistence, analytics, and export.
 */
public class RecordManager {
    private final FileStorageService storageService;
    private final ValidationService validationService;
    private final BloodPressureClassifier bloodPressureClassifier;
    private final LipidClassifier lipidClassifier;
    private final AnalyticsEngine analyticsEngine;
    private final ExportService exportService;
    private final List<HealthRecord> records = new ArrayList<>();

    public RecordManager() {
        this(new FileStorageService(),
                new ValidationService(),
                new BloodPressureClassifier(),
                new LipidClassifier(),
                new AnalyticsEngine(),
                new ExportService());
    }

    public RecordManager(FileStorageService storageService,
                         ValidationService validationService,
                         BloodPressureClassifier bloodPressureClassifier,
                         LipidClassifier lipidClassifier,
                         AnalyticsEngine analyticsEngine,
                         ExportService exportService) {
        this.storageService = storageService;
        this.validationService = validationService;
        this.bloodPressureClassifier = bloodPressureClassifier;
        this.lipidClassifier = lipidClassifier;
        this.analyticsEngine = analyticsEngine;
        this.exportService = exportService;
    }

    /**
     * Initializes storage and loads records into memory.
     */
    public StorageInitializationResult initializeStorage() {
        StorageInitializationResult init = storageService.initializeStorage();
        records.clear();
        records.addAll(init.getRecords());
        return init;
    }

    /**
     * Adds a health record after validation and classification, then persists it.
     */
    public OperationResult<AddRecordResponse> addRecord(HealthRecord inputRecord) {
        ValidationResult validationResult = validationService.validateRecord(inputRecord);
        if (!validationResult.isValid()) {
            return OperationResult.failure("Validation failed.", validationResult.getErrors());
        }

        HealthRecord storedRecord = new HealthRecord(
                inputRecord.getSystolic(),
                inputRecord.getDiastolic(),
                inputRecord.getHeartRate(),
                inputRecord.getTotalCholesterol(),
                inputRecord.getLdl(),
                inputRecord.getHdl(),
                inputRecord.getTriglycerides(),
                inputRecord.getTimeOfDay(),
                inputRecord.getMedTiming(),
                inputRecord.getActivityTiming());

        storedRecord.setSessionId(UUID.randomUUID().toString());
        storedRecord.setTimestampEpochMillis(System.currentTimeMillis());

        String bpCategory = bloodPressureClassifier.classifyBloodPressure(
                storedRecord.getSystolic(),
                storedRecord.getDiastolic());
        storedRecord.setBloodPressureCategory(bpCategory);

        LipidClassification lipid = lipidClassifier.classify(storedRecord);
        storedRecord.setTotalCholesterolCategory(lipid.getTotalCholesterolCategory());
        storedRecord.setLdlCategory(lipid.getLdlCategory());
        storedRecord.setHdlCategory(lipid.getHdlCategory());
        storedRecord.setTriglyceridesCategory(lipid.getTriglyceridesCategory());
        storedRecord.setLipidSummary(lipid.getSummary());

        List<String> alerts = buildRiskAlerts(storedRecord, lipid);
        records.add(storedRecord);

        try {
            storageService.saveRecords(records);
        } catch (IOException e) {
            records.remove(records.size() - 1);
            return OperationResult.failure(
                    "Unable to save health record. Please check file permissions.",
                    List.of(e.getMessage()));
        }

        return OperationResult.success(
                "Record saved successfully.",
                new AddRecordResponse(storedRecord, alerts));
    }

    /**
     * Returns all records sorted by timestamp descending.
     */
    public List<HealthRecord> viewRecords() {
        return records.stream()
                .sorted(Comparator.comparingLong(HealthRecord::getTimestampEpochMillis).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Returns records within a date range (inclusive) sorted descending by timestamp.
     */
    public OperationResult<List<HealthRecord>> filterRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return OperationResult.failure("Start and end dates are required.", List.of("Date range is incomplete."));
        }
        if (endDate.isBefore(startDate)) {
            return OperationResult.failure("Invalid date range.", List.of("End date cannot precede start date."));
        }

        ZoneId zone = ZoneId.systemDefault();
        List<HealthRecord> filtered = records.stream()
                .filter(record -> {
                    LocalDate recordDate = DateTimeUtil.toLocalDate(record.getTimestampEpochMillis(), zone);
                    return !(recordDate.isBefore(startDate) || recordDate.isAfter(endDate));
                })
                .sorted(Comparator.comparingLong(HealthRecord::getTimestampEpochMillis).reversed())
                .collect(Collectors.toList());

        return OperationResult.success("Filtered records retrieved.", filtered);
    }

    /**
     * Computes analytics from current in-memory records.
     */
    public AnalyticsResult viewAnalytics() {
        return analyticsEngine.computeAnalytics(records);
    }

    /**
     * Exports current records to CSV.
     */
    public OperationResult<Path> exportAllRecordsToCsv(Path destination) {
        try {
            Path exported = exportService.exportToCsv(viewRecords(), destination);
            return OperationResult.success("Export completed.", exported);
        } catch (IOException e) {
            return OperationResult.failure("Export failed.", List.of(e.getMessage()));
        }
    }

    public List<HealthRecord> getRecordsSnapshot() {
        return new ArrayList<>(records);
    }

    private List<String> buildRiskAlerts(HealthRecord record, LipidClassification lipidClassification) {
        List<String> alerts = new ArrayList<>();
        if (bloodPressureClassifier.isHighRisk(record.getBloodPressureCategory())) {
            alerts.add("Blood pressure category is high-risk: " + record.getBloodPressureCategory());
        }
        alerts.addAll(lipidClassification.getAlerts());
        return alerts;
    }
}
