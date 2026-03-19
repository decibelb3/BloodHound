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

    public StorageInitializationResult(List<HealthRecord> records,
                                       boolean recoveredFromBackup,
                                       boolean initializedEmptyDataset,
                                       List<String> warnings) {
        this.records = records == null ? new ArrayList<>() : new ArrayList<>(records);
        this.recoveredFromBackup = recoveredFromBackup;
        this.initializedEmptyDataset = initializedEmptyDataset;
        this.warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }

    public List<HealthRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }

    public boolean isRecoveredFromBackup() {
        return recoveredFromBackup;
    }

    public boolean isInitializedEmptyDataset() {
        return initializedEmptyDataset;
    }

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }
}
