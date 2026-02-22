package com.txstate.bloodhound;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class ExportService {
    public static File exportToCSV(Context context, List<BPRecord> records) {
        File file = new File(context.getExternalFilesDir(null), "bloodhound_export.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("SessionID,Timestamp,Systolic,Diastolic,Category\n");
            for (BPRecord r : records) {
                writer.append(r.getSessionId()).append(",")
                      .append(String.valueOf(r.getTimestamp())).append(",")
                      .append(String.valueOf(r.getSystolic())).append(",")
                      .append(String.valueOf(r.getDiastolic())).append(",")
                      .append(r.getAHACategory()).append("\n");
            }
            return file;
        } catch (Exception e) {
            return null;
        }
    }
}