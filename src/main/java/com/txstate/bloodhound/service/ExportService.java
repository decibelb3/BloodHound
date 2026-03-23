package com.txstate.bloodhound.service;

import com.txstate.bloodhound.model.HealthRecord;
import com.txstate.bloodhound.util.DateTimeUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Creates CSV exports from stored records.
 */
public class ExportService {

    /**
     * Writes records to a CSV file at the requested destination.
     *
     * @param records records to export (header is still written when empty)
     * @param destinationFile destination CSV path
     * @return the destination path that was written
     * @throws IOException if parent directories cannot be created or the file cannot be written
     */
    public Path exportToCsv(List<HealthRecord> records, Path destinationFile) throws IOException {
        Path parent = destinationFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        StringBuilder csv = new StringBuilder();
        csv.append("SessionID,Timestamp,Systolic,Diastolic,HeartRate,TotalCholesterol,LDL,HDL,Triglycerides,")
                .append("TimeOfDay,MedTiming,ActivityTiming,BloodPressureCategory,TotalCholesterolCategory,")
                .append("LDLCategory,HDLCategory,TriglyceridesCategory,LipidSummary\n");

        for (HealthRecord record : records) {
            csv.append(csvValue(record.getSessionId())).append(",");
            csv.append(csvValue(DateTimeUtil.formatEpochMillis(record.getTimestampEpochMillis()))).append(",");
            csv.append(csvValue(record.getSystolic())).append(",");
            csv.append(csvValue(record.getDiastolic())).append(",");
            csv.append(csvValue(record.getHeartRate())).append(",");
            csv.append(csvValue(record.getTotalCholesterol())).append(",");
            csv.append(csvValue(record.getLdl())).append(",");
            csv.append(csvValue(record.getHdl())).append(",");
            csv.append(csvValue(record.getTriglycerides())).append(",");
            csv.append(csvValue(record.getTimeOfDay())).append(",");
            csv.append(csvValue(record.getMedTiming())).append(",");
            csv.append(csvValue(record.getActivityTiming())).append(",");
            csv.append(csvValue(record.getBloodPressureCategory())).append(",");
            csv.append(csvValue(record.getTotalCholesterolCategory())).append(",");
            csv.append(csvValue(record.getLdlCategory())).append(",");
            csv.append(csvValue(record.getHdlCategory())).append(",");
            csv.append(csvValue(record.getTriglyceridesCategory())).append(",");
            csv.append(csvValue(record.getLipidSummary())).append("\n");
        }

        Files.writeString(destinationFile, csv.toString(), StandardCharsets.UTF_8);
        return destinationFile;
    }

    private String csvValue(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        String escaped = text.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
