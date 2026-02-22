package com.txstate.bloodhound;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private DatabaseController db;
    private EditText editSystolic, editDiastolic, editHeartRate;
    private Spinner spinnerTimeOfDay, spinnerMedTiming, spinnerActivity;
    private LinearLayout recordsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseController(this);

        editSystolic = findViewById(R.id.editSystolic);
        editDiastolic = findViewById(R.id.editDiastolic);
        editHeartRate = findViewById(R.id.editHeartRate);
        spinnerTimeOfDay = findViewById(R.id.spinnerTimeOfDay);
        spinnerMedTiming = findViewById(R.id.spinnerMedTiming);
        spinnerActivity = findViewById(R.id.spinnerActivity);
        recordsContainer = findViewById(R.id.recordsContainer);

        setupSpinners();
        findViewById(R.id.btnSave).setOnClickListener(v -> saveReading());
        findViewById(R.id.btnExport).setOnClickListener(v -> exportCSV());

        refreshRecords();
    }

    private void setupSpinners() {
        String[] times = {"Morning", "Afternoon", "Evening", "Night"};
        String[] meds = {"Before meal", "After meal", "Fasting", "Other"};
        String[] activities = {"Resting", "After exercise", "Active", "Other"};
        spinnerTimeOfDay.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, times));
        spinnerMedTiming.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, meds));
        spinnerActivity.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, activities));
    }

    private void saveReading() {
        String sysStr = editSystolic.getText().toString().trim();
        String diaStr = editDiastolic.getText().toString().trim();
        if (sysStr.isEmpty() || diaStr.isEmpty()) {
            Toast.makeText(this, "Enter systolic and diastolic", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int systolic = Integer.parseInt(sysStr);
            int diastolic = Integer.parseInt(diaStr);
            int heartRate = 0;
            String hrStr = editHeartRate.getText().toString().trim();
            if (!hrStr.isEmpty()) heartRate = Integer.parseInt(hrStr);

            BPRecord record = new BPRecord(
                    UUID.randomUUID().toString(),
                    System.currentTimeMillis(),
                    systolic,
                    diastolic,
                    heartRate,
                    (String) spinnerTimeOfDay.getSelectedItem(),
                    (String) spinnerMedTiming.getSelectedItem(),
                    (String) spinnerActivity.getSelectedItem());

            db.insertRecord(record);
            editSystolic.setText("");
            editDiastolic.setText("");
            editHeartRate.setText("");
            refreshRecords();
            Toast.makeText(this, "Saved: " + record.getAHACategory(), Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void exportCSV() {
        List<BPRecord> records = db.getAllRecords();
        File file = ExportService.exportToCSV(this, records);
        if (file != null) {
            Toast.makeText(this, "Exported to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshRecords() {
        recordsContainer.removeAllViews();
        List<BPRecord> records = db.getAllRecords();
        for (BPRecord r : records) {
            TextView tv = new TextView(this);
            tv.setPadding(0, 8, 0, 8);
            tv.setText(r.getSystolic() + "/" + r.getDiastolic() + "  " + r.getAHACategory() + "  " + r.getTimeOfDay());
            recordsContainer.addView(tv);
        }
    }
}
