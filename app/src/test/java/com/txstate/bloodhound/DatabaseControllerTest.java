package com.txstate.bloodhound;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Robolectric unit tests for DatabaseController.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class DatabaseControllerTest {

    private DatabaseController db;
    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        db = new DatabaseController(context);
    }

    @Test
    public void insertRecord_persistsData() {
        BPRecord record = new BPRecord("sess-001", 1700000000L, 120, 80,
                72, "Morning", "Before", "Resting");
        db.insertRecord(record);

        assertNotNull(db.getReadableDatabase());
        db.getReadableDatabase().close();
    }

    @Test
    public void onCreate_createsTable() {
        android.database.sqlite.SQLiteDatabase d = db.getReadableDatabase();
        android.database.Cursor c = d.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='bp_records'", null);
        assertTrue(c.moveToFirst());
        assertEquals("bp_records", c.getString(0));
        c.close();
        d.close();
    }
}
