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
        db.execSQL("CREATE TABLE bp_records (")
                .append("session_id TEXT PRIMARY KEY, ")
                .append("timestamp INTEGER, ")
                .append("systolic INTEGER, ")
                .append("diastolic INTEGER, ")
                .append("heart_rate INTEGER, ")
                .append("time_of_day TEXT, ")
                .append("med_timing TEXT, ")
                .append("activity_timing TEXT)");
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
}