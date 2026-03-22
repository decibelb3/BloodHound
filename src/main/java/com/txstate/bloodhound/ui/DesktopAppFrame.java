package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.AddRecordResponse;
import com.txstate.bloodhound.model.AnalyticsResult;
import com.txstate.bloodhound.model.HealthRecord;
import com.txstate.bloodhound.service.RecordManager;
import com.txstate.bloodhound.util.DateTimeUtil;
import com.txstate.bloodhound.util.OperationResult;
import com.txstate.bloodhound.util.StorageInitializationResult;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Swing desktop UI for Bloodhound core use cases.
 */
public class DesktopAppFrame extends JFrame {
    private final RecordManager recordManager;
    private final StorageInitializationResult initializationResult;

    private final JLabel statusLabel = new JLabel("Ready.");
    private final JLabel dashboardRecordCountLabel = new JLabel("Records loaded: 0");
    private final JLabel dashboardStorageLabel = new JLabel("Storage status: Ready");

    private final JTextField systolicField = new JTextField();
    private final JTextField diastolicField = new JTextField();
    private final JTextField heartRateField = new JTextField();
    private final JTextField totalCholesterolField = new JTextField();
    private final JTextField ldlField = new JTextField();
    private final JTextField hdlField = new JTextField();
    private final JTextField triglyceridesField = new JTextField();
    private final JTextField timeOfDayField = new JTextField();
    private final JTextField medTimingField = new JTextField();
    private final JTextField activityTimingField = new JTextField();

    private final JTextField startDateFilterField = new JTextField(10);
    private final JTextField endDateFilterField = new JTextField(10);
    private final DefaultTableModel historyTableModel = new DefaultTableModel(
            new String[]{
                    "Session ID", "Timestamp", "BP Category", "Systolic", "Diastolic", "Heart Rate",
                    "Total Chol", "LDL", "HDL", "Triglycerides", "Tags", "Lipid Summary"
            }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable historyTable = new JTable(historyTableModel);

    private final JTextArea analyticsTextArea = new JTextArea(15, 60);

    private final JTextField exportPathField = new JTextField("exports/bloodhound_export.csv", 35);

    public DesktopAppFrame(RecordManager recordManager, StorageInitializationResult initializationResult) {
        this.recordManager = recordManager;
        this.initializationResult = initializationResult;

        setTitle("Bloodhound - Standalone Health Tracking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);

        initializeLayout();
        refreshAllViews();
        SwingUtilities.invokeLater(this::showStartupMessages);
    }

    private void initializeLayout() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("Bloodhound Dashboard");
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        root.add(header, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", buildDashboardPanel());
        tabbedPane.addTab("Add Record", buildAddRecordPanel());
        tabbedPane.addTab("History", buildHistoryPanel());
        tabbedPane.addTab("Analytics", buildAnalyticsPanel());
        tabbedPane.addTab("Export", buildExportPanel());

        root.add(tabbedPane, BorderLayout.CENTER);
        root.add(statusLabel, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildDashboardPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("System Status"));
        panel.add(dashboardRecordCountLabel);
        panel.add(dashboardStorageLabel);
        panel.add(new JLabel("Use the tabs above to add records, review history, view analytics, and export CSV."));
        return panel;
    }

    private JPanel buildAddRecordPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Health Record"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Systolic", systolicField);
        addFormRow(formPanel, gbc, row++, "Diastolic", diastolicField);
        addFormRow(formPanel, gbc, row++, "Heart Rate", heartRateField);
        addFormRow(formPanel, gbc, row++, "Total Cholesterol", totalCholesterolField);
        addFormRow(formPanel, gbc, row++, "LDL", ldlField);
        addFormRow(formPanel, gbc, row++, "HDL", hdlField);
        addFormRow(formPanel, gbc, row++, "Triglycerides", triglyceridesField);
        addFormRow(formPanel, gbc, row++, "Time of Day", timeOfDayField);
        addFormRow(formPanel, gbc, row++, "Medication Timing", medTimingField);
        addFormRow(formPanel, gbc, row++, "Activity Timing", activityTimingField);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton saveButton = new JButton("Save Record");
        saveButton.addActionListener(e -> handleSaveRecord());
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearAddRecordForm());
        actions.add(saveButton);
        actions.add(clearButton);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Date Range Filter (YYYY-MM-DD)"));
        filterPanel.add(new JLabel("Start:"));
        filterPanel.add(startDateFilterField);
        filterPanel.add(new JLabel("End:"));
        filterPanel.add(endDateFilterField);

        JButton applyFilterButton = new JButton("Apply Filter");
        applyFilterButton.addActionListener(e -> handleApplyDateFilter());
        JButton clearFilterButton = new JButton("Clear Filter");
        clearFilterButton.addActionListener(e -> handleClearDateFilter());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshHistoryTable(recordManager.viewRecords()));
        filterPanel.add(applyFilterButton);
        filterPanel.add(clearFilterButton);
        filterPanel.add(refreshButton);

        historyTable.setAutoCreateRowSorter(true);
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        analyticsTextArea.setEditable(false);
        analyticsTextArea.setLineWrap(true);
        analyticsTextArea.setWrapStyleWord(true);
        analyticsTextArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton refreshAnalyticsButton = new JButton("Refresh Analytics");
        refreshAnalyticsButton.addActionListener(e -> refreshAnalyticsView());
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(refreshAnalyticsButton);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(new JScrollPane(analyticsTextArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildExportPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setBorder(BorderFactory.createTitledBorder("Export to CSV"));

        controls.add(new JLabel("Destination:"));
        controls.add(exportPathField);

        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(e -> handleBrowseExportDestination());
        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(e -> handleExportRecords());

        controls.add(browseButton);
        controls.add(exportButton);

        JTextArea note = new JTextArea(
                "Export includes all currently stored records with metric and category fields.\n"
                        + "If no records exist, the CSV will contain headers only.");
        note.setEditable(false);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        note.setBackground(panel.getBackground());

        panel.add(controls, BorderLayout.NORTH);
        panel.add(note, BorderLayout.CENTER);
        return panel;
    }

    private void addFormRow(JPanel formPanel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel(label + ":"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(field, gbc);
    }

    private void handleSaveRecord() {
        try {
            HealthRecord draft = new HealthRecord();
            draft.setSystolic(UiRecordHelper.parseOptionalInteger("Systolic", systolicField.getText()));
            draft.setDiastolic(UiRecordHelper.parseOptionalInteger("Diastolic", diastolicField.getText()));
            draft.setHeartRate(UiRecordHelper.parseOptionalInteger("Heart Rate", heartRateField.getText()));
            draft.setTotalCholesterol(UiRecordHelper.parseOptionalInteger("Total Cholesterol", totalCholesterolField.getText()));
            draft.setLdl(UiRecordHelper.parseOptionalInteger("LDL", ldlField.getText()));
            draft.setHdl(UiRecordHelper.parseOptionalInteger("HDL", hdlField.getText()));
            draft.setTriglycerides(UiRecordHelper.parseOptionalInteger("Triglycerides", triglyceridesField.getText()));
            draft.setTimeOfDay(UiRecordHelper.nullIfBlank(timeOfDayField.getText()));
            draft.setMedTiming(UiRecordHelper.nullIfBlank(medTimingField.getText()));
            draft.setActivityTiming(UiRecordHelper.nullIfBlank(activityTimingField.getText()));

            OperationResult<AddRecordResponse> result = recordManager.addRecord(draft);
            if (!result.isSuccess()) {
                showError(result.getMessage() + "\n- " + String.join("\n- ", result.getErrors()));
                setStatus("Save failed.");
                return;
            }

            AddRecordResponse payload = result.getData();
            StringBuilder message = new StringBuilder(result.getMessage())
                    .append("\nSession ID: ").append(payload.getRecord().getSessionId());
            if (!payload.getAlerts().isEmpty()) {
                message.append("\n\nRisk alerts:\n- ").append(String.join("\n- ", payload.getAlerts()));
            }
            JOptionPane.showMessageDialog(this, message.toString(), "Record Saved", JOptionPane.INFORMATION_MESSAGE);
            clearAddRecordForm();
            refreshAllViews();
            setStatus("Record saved at " + DateTimeUtil.formatEpochMillis(payload.getRecord().getTimestampEpochMillis()));
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void handleApplyDateFilter() {
        try {
            LocalDate startDate = UiRecordHelper.parseDateField("Start Date", startDateFilterField.getText());
            LocalDate endDate = UiRecordHelper.parseDateField("End Date", endDateFilterField.getText());

            OperationResult<List<HealthRecord>> result = recordManager.filterRecordsByDateRange(startDate, endDate);
            if (!result.isSuccess()) {
                showError(result.getMessage() + "\n- " + String.join("\n- ", result.getErrors()));
                return;
            }
            refreshHistoryTable(result.getData());
            setStatus("Showing " + result.getData().size() + " filtered record(s).");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void handleClearDateFilter() {
        startDateFilterField.setText("");
        endDateFilterField.setText("");
        refreshHistoryTable(recordManager.viewRecords());
        setStatus("Date filter cleared.");
    }

    private void handleExportRecords() {
        String destinationText = exportPathField.getText().trim();
        if (destinationText.isBlank()) {
            showError("Please provide an export destination path.");
            return;
        }

        OperationResult<Path> result = recordManager.exportAllRecordsToCsv(Path.of(destinationText));
        if (!result.isSuccess()) {
            showError(result.getMessage() + "\n- " + String.join("\n- ", result.getErrors()));
            setStatus("Export failed.");
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Export successful:\n" + result.getData().toAbsolutePath(),
                "Export Complete",
                JOptionPane.INFORMATION_MESSAGE);
        setStatus("Export complete: " + result.getData().toAbsolutePath());
    }

    private void handleBrowseExportDestination() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose export CSV destination");
        int selection = fileChooser.showSaveDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
            exportPathField.setText(fileChooser.getSelectedFile().toPath().toString());
        }
    }

    private void refreshAllViews() {
        refreshHistoryTable(recordManager.viewRecords());
        refreshAnalyticsView();
        dashboardRecordCountLabel.setText("Records loaded: " + recordManager.getRecordsSnapshot().size());
    }

    private void refreshHistoryTable(List<HealthRecord> records) {
        historyTableModel.setRowCount(0);
        for (HealthRecord record : records) {
            historyTableModel.addRow(new Object[]{
                    UiRecordHelper.valueOrBlank(record.getSessionId()),
                    DateTimeUtil.formatEpochMillis(record.getTimestampEpochMillis()),
                    UiRecordHelper.valueOrBlank(record.getBloodPressureCategory()),
                    UiRecordHelper.valueOrBlank(record.getSystolic()),
                    UiRecordHelper.valueOrBlank(record.getDiastolic()),
                    UiRecordHelper.valueOrBlank(record.getHeartRate()),
                    UiRecordHelper.valueOrBlank(record.getTotalCholesterol()),
                    UiRecordHelper.valueOrBlank(record.getLdl()),
                    UiRecordHelper.valueOrBlank(record.getHdl()),
                    UiRecordHelper.valueOrBlank(record.getTriglycerides()),
                    UiRecordHelper.buildTagSummary(record),
                    UiRecordHelper.valueOrBlank(record.getLipidSummary())
            });
        }
    }

    private void refreshAnalyticsView() {
        AnalyticsResult analytics = recordManager.viewAnalytics();
        StringBuilder builder = new StringBuilder();
        builder.append("Averages").append("\n")
                .append(" - Systolic: ").append(UiRecordHelper.formatDouble(analytics.getAverageSystolic())).append("\n")
                .append(" - Diastolic: ").append(UiRecordHelper.formatDouble(analytics.getAverageDiastolic())).append("\n")
                .append(" - Heart Rate: ").append(UiRecordHelper.formatDouble(analytics.getAverageHeartRate())).append("\n")
                .append(" - Total Cholesterol: ").append(UiRecordHelper.formatDouble(analytics.getAverageTotalCholesterol())).append("\n")
                .append(" - LDL: ").append(UiRecordHelper.formatDouble(analytics.getAverageLdl())).append("\n")
                .append(" - HDL: ").append(UiRecordHelper.formatDouble(analytics.getAverageHdl())).append("\n")
                .append(" - Triglycerides: ").append(UiRecordHelper.formatDouble(analytics.getAverageTriglycerides())).append("\n\n")
                .append("Blood Pressure Category Counts").append("\n");

        if (analytics.getBloodPressureCategoryCounts().isEmpty()) {
            builder.append(" - No records.\n");
        } else {
            for (Map.Entry<String, Integer> entry : analytics.getBloodPressureCategoryCounts().entrySet()) {
                builder.append(" - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        builder.append("\nAlerts\n");
        for (String alert : analytics.getAlertSummaries()) {
            builder.append(" - ").append(alert).append("\n");
        }
        builder.append("\nTrend\n - ").append(UiRecordHelper.valueOrBlank(analytics.getTrendSummary()));

        analyticsTextArea.setText(builder.toString());
        analyticsTextArea.setCaretPosition(0);
    }

    private void showStartupMessages() {
        dashboardStorageLabel.setText(buildStorageStatusText());
        if (!initializationResult.getWarnings().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    String.join("\n", initializationResult.getWarnings()),
                    "Startup Recovery Notice",
                    JOptionPane.WARNING_MESSAGE);
        }
        setStatus("Startup complete. Loaded " + initializationResult.getRecords().size() + " record(s).");
    }

    private String buildStorageStatusText() {
        if (initializationResult.isRecoveredFromBackup()) {
            return "Storage status: Recovered from backup";
        }
        if (initializationResult.isInitializedEmptyDataset()) {
            return "Storage status: Initialized empty dataset";
        }
        return "Storage status: Primary storage loaded";
    }

    private void clearAddRecordForm() {
        systolicField.setText("");
        diastolicField.setText("");
        heartRateField.setText("");
        totalCholesterolField.setText("");
        ldlField.setText("");
        hdlField.setText("");
        triglyceridesField.setText("");
        timeOfDayField.setText("");
        medTimingField.setText("");
        activityTimingField.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}
