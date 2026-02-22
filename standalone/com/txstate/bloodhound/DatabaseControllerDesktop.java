package com.txstate.bloodhound;

import java.io.*;
import java.util.*;

/**
 * Standalone (non-Android) database controller using a simple file for storage.
 */
public class DatabaseControllerDesktop {
    private final File dataFile;

    public DatabaseControllerDesktop() {
        this.dataFile = new File("bloodhound_data.txt");
    }

    public void insertRecord(BPRecord record) {
        try (FileWriter fw = new FileWriter(dataFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(String.join("\t",
                    record.getSessionId(),
                    String.valueOf(record.getTimestamp()),
                    String.valueOf(record.getSystolic()),
                    String.valueOf(record.getDiastolic()),
                    String.valueOf(record.getHeartRate()),
                    nullToEmpty(record.getTimeOfDay()),
                    nullToEmpty(record.getMedTiming()),
                    nullToEmpty(record.getActivityTiming()),
                    String.valueOf(record.getTotalCholesterol()),
                    String.valueOf(record.getLdl()),
                    String.valueOf(record.getHdl())));
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save record", e);
        }
    }

    public List<BPRecord> getAllRecords() {
        List<BPRecord> list = new ArrayList<>();
        if (!dataFile.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t", -1);
                if (parts.length >= 8) {
                    int totalCholesterol = parts.length > 8 ? parseIntSafe(parts[8]) : 0;
                    int ldl = parts.length > 9 ? parseIntSafe(parts[9]) : 0;
                    int hdl = parts.length > 10 ? parseIntSafe(parts[10]) : 0;
                    list.add(new BPRecord(
                            parts[0],
                            Long.parseLong(parts[1]),
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4]),
                            parts[5],
                            parts[6],
                            parts[7],
                            totalCholesterol,
                            ldl,
                            hdl));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read records", e);
        }
        list.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        return list;
    }

    public boolean deleteBySessionId(String sessionId) {
        List<BPRecord> records = getAllRecords();
        boolean removed = records.removeIf(r -> r.getSessionId().equals(sessionId));
        if (!removed) return false;
        writeAll(records);
        return true;
    }

    public boolean updateRecord(BPRecord updated) {
        List<BPRecord> records = getAllRecords();
        boolean replaced = false;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getSessionId().equals(updated.getSessionId())) {
                records.set(i, updated);
                replaced = true;
                break;
            }
        }
        if (!replaced) return false;
        writeAll(records);
        return true;
    }

    private void writeAll(List<BPRecord> records) {
        try (FileWriter fw = new FileWriter(dataFile, false);
             BufferedWriter bw = new BufferedWriter(fw)) {
            for (BPRecord record : records) {
                bw.write(String.join("\t",
                        record.getSessionId(),
                        String.valueOf(record.getTimestamp()),
                        String.valueOf(record.getSystolic()),
                        String.valueOf(record.getDiastolic()),
                        String.valueOf(record.getHeartRate()),
                        nullToEmpty(record.getTimeOfDay()),
                        nullToEmpty(record.getMedTiming()),
                        nullToEmpty(record.getActivityTiming()),
                        String.valueOf(record.getTotalCholesterol()),
                        String.valueOf(record.getLdl()),
                        String.valueOf(record.getHdl())));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to rewrite records", e);
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
