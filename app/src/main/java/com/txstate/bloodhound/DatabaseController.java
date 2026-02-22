package com.txstate.bloodhound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseController extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bloodhound.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bp_records (" +
                "session_id TEXT PRIMARY KEY, " +
                "timestamp INTEGER, " +
                "systolic INTEGER, " +
                "diastolic INTEGER, " +
                "heart_rate INTEGER, " +
                "time_of_day TEXT, " +
                "med_timing TEXT, " +
                "activity_timing TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS bp_records");
        onCreate(db);
    }

    public void insertRecord(BPRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("session_id", record.getSessionId());
        values.put("timestamp", record.getTimestamp());
        values.put("systolic", record.getSystolic());
        values.put("diastolic", record.getDiastolic());
        values.put("heart_rate", record.getHeartRate());
        values.put("time_of_day", record.getTimeOfDay());
        values.put("med_timing", record.getMedTiming());
        values.put("activity_timing", record.getActivityTiming());
        db.insert("bp_records", null, values);
    }

    public java.util.List<BPRecord> getAllRecords() {
        java.util.List<BPRecord> list = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("bp_records", null, null, null, null, null, "timestamp DESC");
        while (c != null && c.moveToNext()) {
            list.add(new BPRecord(
                    c.getString(c.getColumnIndexOrThrow("session_id")),
                    c.getLong(c.getColumnIndexOrThrow("timestamp")),
                    c.getInt(c.getColumnIndexOrThrow("systolic")),
                    c.getInt(c.getColumnIndexOrThrow("diastolic")),
                    c.getInt(c.getColumnIndexOrThrow("heart_rate")),
                    c.getString(c.getColumnIndexOrThrow("time_of_day")),
                    c.getString(c.getColumnIndexOrThrow("med_timing")),
                    c.getString(c.getColumnIndexOrThrow("activity_timing"))));
        }
        if (c != null) c.close();
        return list;
    }
}
