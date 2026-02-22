package com.txstate.bloodhound;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Robolectric unit tests for ExportService.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ExportServiceTest {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
    }

    @Test
    public void exportToCSV_createsFileWithHeader() throws Exception {
        List<BPRecord> records = new ArrayList<>();
        records.add(new BPRecord("s1", 1000L, 120, 80, 70, "AM", "Before", "Rest"));

        File file = ExportService.exportToCSV(context, records);
        assertNotNull(file);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        assertTrue(content.startsWith("SessionID,Timestamp,Systolic,Diastolic,Category\n"));
    }

    @Test
    public void exportToCSV_includesRecordData() throws Exception {
        List<BPRecord> records = new ArrayList<>();
        records.add(new BPRecord("sess-1", 12345L, 130, 85, 72, "Evening", "After", "Active"));

        File file = ExportService.exportToCSV(context, records);
        assertNotNull(file);
        String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        assertTrue(content.contains("sess-1"));
        assertTrue(content.contains("12345"));
        assertTrue(content.contains("130"));
        assertTrue(content.contains("85"));
        assertTrue(content.contains("Stage 1"));
    }

    @Test
    public void exportToCSV_emptyList_createsHeaderOnly() throws Exception {
        List<BPRecord> records = new ArrayList<>();
        File file = ExportService.exportToCSV(context, records);
        assertNotNull(file);
        String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        assertEquals("SessionID,Timestamp,Systolic,Diastolic,Category\n", content);
    }
}
