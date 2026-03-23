package com.txstate.bloodhound.util;

import com.txstate.bloodhound.model.HealthRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Startup result for storage initialization and potential recovery.
 */
public class StorageInitializationResult {
    private final List<HealthRecord> records;
    private final boolean recoveredFromBackup;
    private final boolean initializedEmptyDataset;
    private final List<String> warnings;

    /**
     * Creates a storage startup result.
     *
     * @param records records loaded from storage
     * @param recoveredFromBackup whether backup recovery was used
     * @param initializedEmptyDataset whether an empty dataset was initialized
     * @param warnings warning messages encountered during startup
     */
    public StorageInitializationResult(List<HealthRecord> records,
                                       boolean recoveredFromBackup,
                                       boolean initializedEmptyDataset,
                                       List<String> warnings) {
        this.records = records == null ? new ArrayList<>() : new ArrayList<>(records);
        this.recoveredFromBackup = recoveredFromBackup;
        this.initializedEmptyDataset = initializedEmptyDataset;
        this.warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }

    /**
     * Returns loaded records.
     *
     * @return immutable list of loaded records
     */
    public List<HealthRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }

    /**
     * Indicates whether startup used backup recovery.
     *
     * @return {@code true} when backup recovery was used
     */
    public boolean isRecoveredFromBackup() {
        return recoveredFromBackup;
    }

    /**
     * Indicates whether startup initialized empty dataset files.
     *
     * @return {@code true} when empty dataset initialization occurred
     */
    public boolean isInitializedEmptyDataset() {
        return initializedEmptyDataset;
    }

    /**
     * Returns startup warning messages.
     *
     * @return immutable list of warning messages
     */
    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }
}
