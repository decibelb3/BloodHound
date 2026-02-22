package com.txstate.bloodhound;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Standalone (non-Android) export service.
 */
public class ExportServiceDesktop {
    public static File exportToCSV(File outputDir, List<BPRecord> records) {
        File file = new File(outputDir, "bloodhound_export.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("SessionID,Timestamp,Systolic,Diastolic,HeartRate,TimeOfDay,MedTiming,ActivityTiming,TotalCholesterol,LDL,HDL,Category\n");
            for (BPRecord r : records) {
                writer.append(r.getSessionId()).append(",")
                      .append(String.valueOf(r.getTimestamp())).append(",")
                      .append(String.valueOf(r.getSystolic())).append(",")
                      .append(String.valueOf(r.getDiastolic())).append(",")
                      .append(String.valueOf(r.getHeartRate())).append(",")
                      .append(r.getTimeOfDay()).append(",")
                      .append(r.getMedTiming()).append(",")
                      .append(r.getActivityTiming()).append(",")
                      .append(String.valueOf(r.getTotalCholesterol())).append(",")
                      .append(String.valueOf(r.getLdl())).append(",")
                      .append(String.valueOf(r.getHdl())).append(",")
                      .append(r.getAHACategory()).append("\n");
            }
            return file;
        } catch (Exception e) {
            return null;
        }
    }
}
