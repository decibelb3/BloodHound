package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.AddRecordResponse;
import com.txstate.bloodhound.model.AnalyticsResult;
import com.txstate.bloodhound.model.HealthRecord;
import com.txstate.bloodhound.service.RecordManager;
import com.txstate.bloodhound.util.DateTimeUtil;
import com.txstate.bloodhound.util.OperationResult;
import com.txstate.bloodhound.util.StorageInitializationResult;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Simple console UI for core Bloodhound use cases.
 */
public class ConsoleApp {
    private final RecordManager recordManager;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(RecordManager recordManager) {
        this.recordManager = recordManager;
    }

    public void run() {
        StorageInitializationResult init = recordManager.initializeStorage();
        printStartupStatus(init);

        boolean running = true;
        while (running) {
            printMenu();
            String selection = scanner.nextLine().trim();
            switch (selection) {
                case "1":
                    handleAddRecord();
                    break;
                case "2":
                    handleViewRecords(recordManager.viewRecords());
                    break;
                case "3":
                    handleDateFilter();
                    break;
                case "4":
                    handleViewAnalytics();
                    break;
                case "5":
                    handleExport();
                    break;
                case "0":
                    running = false;
                    System.out.println("Exiting Bloodhound.");
                    break;
                default:
                    System.out.println("Invalid menu option.");
            }
        }
    }

    private void printStartupStatus(StorageInitializationResult init) {
        if (init.getWarnings().isEmpty()) {
            System.out.println("Storage initialized successfully.");
        } else {
            init.getWarnings().forEach(System.out::println);
        }
        System.out.println("Loaded records: " + init.getRecords().size());
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Bloodhound Menu ===");
        System.out.println("1) Add Health Record");
        System.out.println("2) View Records");
        System.out.println("3) Filter Records by Date Range");
        System.out.println("4) View Analytics");
        System.out.println("5) Export Data");
        System.out.println("0) Exit");
        System.out.print("Choose an option: ");
    }

    private void handleAddRecord() {
        HealthRecord draft = new HealthRecord();

        draft.setSystolic(promptOptionalInt("Systolic (optional): "));
        draft.setDiastolic(promptOptionalInt("Diastolic (optional): "));
        draft.setHeartRate(promptOptionalInt("Heart rate (optional): "));
        draft.setTotalCholesterol(promptOptionalInt("Total cholesterol (optional): "));
        draft.setLdl(promptOptionalInt("LDL (optional): "));
        draft.setHdl(promptOptionalInt("HDL (optional): "));
        draft.setTriglycerides(promptOptionalInt("Triglycerides (optional): "));
        draft.setTimeOfDay(promptOptionalString("Time of day (optional): "));
        draft.setMedTiming(promptOptionalString("Medication timing (optional): "));
        draft.setActivityTiming(promptOptionalString("Activity timing (optional): "));

        OperationResult<AddRecordResponse> result = recordManager.addRecord(draft);
        if (!result.isSuccess()) {
            System.out.println(result.getMessage());
            result.getErrors().forEach(error -> System.out.println("- " + error));
            return;
        }

        AddRecordResponse payload = result.getData();
        System.out.println(result.getMessage());
        System.out.println("Session ID: " + payload.getRecord().getSessionId());
        if (!payload.getAlerts().isEmpty()) {
            System.out.println("Risk alerts:");
            payload.getAlerts().forEach(alert -> System.out.println("- " + alert));
        }
    }

    private void handleViewRecords(List<HealthRecord> records) {
        if (records.isEmpty()) {
            System.out.println("No records available.");
            return;
        }

        System.out.println("Displaying " + records.size() + " record(s):");
        for (HealthRecord record : records) {
            System.out.println("------------------------------------------------");
            System.out.println("Session: " + record.getSessionId());
            System.out.println("Timestamp: " + DateTimeUtil.formatEpochMillis(record.getTimestampEpochMillis()));
            System.out.println("BP: " + printable(record.getSystolic()) + "/" + printable(record.getDiastolic())
                    + " (" + printable(record.getBloodPressureCategory()) + ")");
            System.out.println("Heart Rate: " + printable(record.getHeartRate()));
            System.out.println("Lipids: TC " + printable(record.getTotalCholesterol())
                    + ", LDL " + printable(record.getLdl())
                    + ", HDL " + printable(record.getHdl())
                    + ", TG " + printable(record.getTriglycerides()));
            System.out.println("Lipid Summary: " + printable(record.getLipidSummary()));
            System.out.println("Tags: timeOfDay=" + printable(record.getTimeOfDay())
                    + ", medTiming=" + printable(record.getMedTiming())
                    + ", activityTiming=" + printable(record.getActivityTiming()));
        }
    }

    private void handleDateFilter() {
        try {
            System.out.print("Start date (YYYY-MM-DD): ");
            LocalDate start = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("End date (YYYY-MM-DD): ");
            LocalDate end = LocalDate.parse(scanner.nextLine().trim());

            OperationResult<List<HealthRecord>> result = recordManager.filterRecordsByDateRange(start, end);
            if (!result.isSuccess()) {
                System.out.println(result.getMessage());
                result.getErrors().forEach(error -> System.out.println("- " + error));
                return;
            }
            handleViewRecords(result.getData());
        } catch (Exception e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
        }
    }

    private void handleViewAnalytics() {
        AnalyticsResult analytics = recordManager.viewAnalytics();
        System.out.println("=== Analytics ===");
        System.out.println("Avg Systolic: " + printable(analytics.getAverageSystolic()));
        System.out.println("Avg Diastolic: " + printable(analytics.getAverageDiastolic()));
        System.out.println("Avg Heart Rate: " + printable(analytics.getAverageHeartRate()));
        System.out.println("Avg Total Cholesterol: " + printable(analytics.getAverageTotalCholesterol()));
        System.out.println("Avg LDL: " + printable(analytics.getAverageLdl()));
        System.out.println("Avg HDL: " + printable(analytics.getAverageHdl()));
        System.out.println("Avg Triglycerides: " + printable(analytics.getAverageTriglycerides()));

        System.out.println("Blood Pressure Category Counts:");
        for (Map.Entry<String, Integer> entry : analytics.getBloodPressureCategoryCounts().entrySet()) {
            System.out.println("- " + entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("Alerts:");
        analytics.getAlertSummaries().forEach(alert -> System.out.println("- " + alert));
        System.out.println("Trend: " + printable(analytics.getTrendSummary()));
    }

    private void handleExport() {
        System.out.print("Export destination (blank for exports/bloodhound_export.csv): ");
        String pathInput = scanner.nextLine().trim();
        Path destination = pathInput.isBlank()
                ? Path.of("exports", "bloodhound_export.csv")
                : Path.of(pathInput);

        OperationResult<Path> exportResult = recordManager.exportAllRecordsToCsv(destination);
        if (!exportResult.isSuccess()) {
            System.out.println(exportResult.getMessage());
            exportResult.getErrors().forEach(error -> System.out.println("- " + error));
            return;
        }
        System.out.println("Export successful: " + exportResult.getData().toAbsolutePath());
    }

    private Integer promptOptionalInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isBlank()) {
                return null;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a numeric value or leave blank.");
            }
        }
    }

    private String promptOptionalString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isBlank() ? null : input;
    }

    private String printable(Object value) {
        return value == null ? "N/A" : String.valueOf(value);
    }
}
