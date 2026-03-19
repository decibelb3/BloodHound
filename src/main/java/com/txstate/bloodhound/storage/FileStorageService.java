package com.txstate.bloodhound.storage;

import com.txstate.bloodhound.model.HealthRecord;
import com.txstate.bloodhound.util.JsonUtil;
import com.txstate.bloodhound.util.StorageInitializationResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles local JSON persistence, backup creation, and startup recovery.
 */
public class FileStorageService {
    public static final String DEFAULT_STORAGE_FILE = "health_records.json";
    public static final String DEFAULT_BACKUP_FILE = "health_records.backup.json";

    private final Path primaryFile;
    private final Path backupFile;

    public FileStorageService() {
        this(Path.of(DEFAULT_STORAGE_FILE), Path.of(DEFAULT_BACKUP_FILE));
    }

    public FileStorageService(Path primaryFile, Path backupFile) {
        this.primaryFile = primaryFile;
        this.backupFile = backupFile;
    }

    /**
     * Ensures storage exists and loads records. If the primary file is malformed, attempts backup recovery.
     */
    public StorageInitializationResult initializeStorage() {
        List<String> warnings = new ArrayList<>();
        try {
            ensureParentDirectories();
            if (Files.notExists(primaryFile)) {
                writeRecordsInternal(new ArrayList<>(), primaryFile);
                copyIfExists(primaryFile, backupFile);
                return new StorageInitializationResult(List.of(), false, true, warnings);
            }

            List<HealthRecord> loaded = loadRecords(primaryFile);
            if (Files.notExists(backupFile)) {
                copyIfExists(primaryFile, backupFile);
            }
            return new StorageInitializationResult(loaded, false, false, warnings);
        } catch (Exception primaryError) {
            warnings.add("Storage file was corrupted. Attempting recovery from backup.");
            try {
                List<HealthRecord> recovered = loadRecords(backupFile);
                writeRecordsInternal(recovered, primaryFile);
                warnings.add("Storage file was corrupted. Recovery from backup succeeded.");
                return new StorageInitializationResult(recovered, true, false, warnings);
            } catch (Exception backupError) {
                warnings.add("Storage file could not be recovered. A new storage file has been created.");
                try {
                    writeRecordsInternal(new ArrayList<>(), primaryFile);
                    writeRecordsInternal(new ArrayList<>(), backupFile);
                } catch (IOException ignored) {
                    warnings.add("Failed to initialize clean storage files.");
                }
                return new StorageInitializationResult(List.of(), false, true, warnings);
            }
        }
    }

    /**
     * Loads records from the primary storage file.
     */
    public List<HealthRecord> loadRecords() throws IOException {
        return loadRecords(primaryFile);
    }

    /**
     * Saves records to primary storage while maintaining a backup copy.
     */
    public void saveRecords(List<HealthRecord> records) throws IOException {
        ensureParentDirectories();
        if (Files.exists(primaryFile)) {
            copyIfExists(primaryFile, backupFile);
        }

        Path tempFile = primaryFile.resolveSibling(primaryFile.getFileName() + ".tmp");
        writeRecordsInternal(records, tempFile);
        Files.move(tempFile, primaryFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        copyIfExists(primaryFile, backupFile);
    }

    /**
     * Attempts an explicit recovery from backup into primary storage.
     */
    public List<HealthRecord> recoverFromBackup() throws IOException {
        List<HealthRecord> recovered = loadRecords(backupFile);
        writeRecordsInternal(recovered, primaryFile);
        return recovered;
    }

    public Path getPrimaryFile() {
        return primaryFile;
    }

    public Path getBackupFile() {
        return backupFile;
    }

    private List<HealthRecord> loadRecords(Path path) throws IOException {
        if (Files.notExists(path)) {
            return new ArrayList<>();
        }
        String raw = Files.readString(path, StandardCharsets.UTF_8);
        return JsonUtil.fromJson(raw);
    }

    private void writeRecordsInternal(List<HealthRecord> records, Path destination) throws IOException {
        ensureParentDirectories();
        String json = JsonUtil.toJson(records == null ? List.of() : records);
        Files.writeString(destination, json, StandardCharsets.UTF_8);
    }

    private void ensureParentDirectories() throws IOException {
        Path primaryParent = primaryFile.getParent();
        if (primaryParent != null) {
            Files.createDirectories(primaryParent);
        }
        Path backupParent = backupFile.getParent();
        if (backupParent != null) {
            Files.createDirectories(backupParent);
        }
    }

    private void copyIfExists(Path source, Path destination) throws IOException {
        if (Files.exists(source)) {
            Path destinationParent = destination.getParent();
            if (destinationParent != null) {
                Files.createDirectories(destinationParent);
            }
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
