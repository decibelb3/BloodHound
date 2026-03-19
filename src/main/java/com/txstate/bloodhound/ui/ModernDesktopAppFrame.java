package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.AddRecordResponse;
import com.txstate.bloodhound.model.AnalyticsResult;
import com.txstate.bloodhound.model.HealthRecord;
import com.txstate.bloodhound.service.RecordManager;
import com.txstate.bloodhound.util.DateTimeUtil;
import com.txstate.bloodhound.util.OperationResult;
import com.txstate.bloodhound.util.StorageInitializationResult;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Modern themed Swing desktop UI variant for Bloodhound.
 */
public class ModernDesktopAppFrame extends JFrame {
    private static final Color BG_APP = new Color(16, 18, 23);
    private static final Color BG_PANEL = new Color(28, 31, 39);
    private static final Color BG_PANEL_ALT = new Color(33, 36, 45);
    private static final Color BG_SIDEBAR = new Color(21, 24, 31);
    private static final Color ACCENT = new Color(69, 142, 255);
    private static final Color TEXT_PRIMARY = new Color(235, 238, 245);
    private static final Color TEXT_MUTED = new Color(163, 172, 190);
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    private static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);

    private final RecordManager recordManager;
    private final StorageInitializationResult initializationResult;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(cardLayout);
    private final JLabel statusLabel = new JLabel("Ready.");
    private final JLabel dashboardRecordCountLabel = new JLabel("Records loaded: 0");
    private final JLabel dashboardStorageLabel = new JLabel("Storage status: Ready");
    private final JLabel dashboardTrendLabel = new JLabel("Trend: N/A");

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
    private final JTextArea analyticsTextArea = new JTextArea();
    private final JTextField exportPathField = new JTextField("exports/bloodhound_export_modern.csv", 35);

    public ModernDesktopAppFrame(RecordManager recordManager, StorageInitializationResult initializationResult) {
        this.recordManager = recordManager;
        this.initializationResult = initializationResult;

        setTitle("Bloodhound Modern Desktop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1360, 840);
        setLocationRelativeTo(null);

        initializeLayout();
        refreshAllViews();
        SwingUtilities.invokeLater(this::showStartupMessages);
    }

    private void initializeLayout() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_APP);

        root.add(buildHeaderBar(), BorderLayout.NORTH);
        root.add(buildMainBody(), BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_APP);
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel title = new JLabel("Bloodhound");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Offline Health Tracking · Modern UI Preview");
        subtitle.setFont(FONT_BODY);
        subtitle.setForeground(TEXT_MUTED);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(2));
        titleBlock.add(subtitle);

        header.add(titleBlock, BorderLayout.WEST);
        return header;
    }

    private JPanel buildMainBody() {
        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setBackground(BG_APP);
        body.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));

        body.add(buildSideNavigation(), BorderLayout.WEST);
        body.add(buildContentCards(), BorderLayout.CENTER);
        return body;
    }

    private JPanel buildSideNavigation() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 14, 16, 14));

        sidebar.add(createNavButton("Dashboard", "dashboard"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("Add Record", "add"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("History", "history"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("Analytics", "analytics"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("Export", "export"));
        sidebar.add(Box.createVerticalGlue());

        JLabel helper = new JLabel("<html><div style='width:170px'>Tip: Add a record, then switch to Analytics to see averages and alerts update immediately.</div></html>");
        helper.setForeground(TEXT_MUTED);
        helper.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sidebar.add(helper);
        return sidebar;
    }

    private JPanel buildContentCards() {
        contentCards.setOpaque(false);
        contentCards.add(buildDashboardPanel(), "dashboard");
        contentCards.add(buildAddRecordPanel(), "add");
        contentCards.add(buildHistoryPanel(), "history");
        contentCards.add(buildAnalyticsPanel(), "analytics");
        contentCards.add(buildExportPanel(), "export");
        cardLayout.show(contentCards, "dashboard");
        return contentCards;
    }

    private JPanel buildStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(BG_SIDEBAR);
        statusBar.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        statusLabel.setForeground(TEXT_MUTED);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }

    private JPanel buildDashboardPanel() {
        JPanel container = createCardContainer();
        container.setLayout(new GridLayout(2, 2, 12, 12));

        container.add(createInfoCard("Records Loaded", dashboardRecordCountLabel));
        container.add(createInfoCard("Storage", dashboardStorageLabel));
        container.add(createInfoCard("Trend Summary", dashboardTrendLabel));
        container.add(createStaticCard(
                "Actions",
                "Use the navigation on the left to add health records, review history, compute analytics, and export CSV."));
        return container;
    }

    private JPanel buildAddRecordPanel() {
        JPanel container = createCardContainer();
        container.setLayout(new BorderLayout(12, 12));

        JPanel formCard = createPanelCard("Add Health Record");
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        addFormRow(formCard, gbc, row++, "Systolic", systolicField);
        addFormRow(formCard, gbc, row++, "Diastolic", diastolicField);
        addFormRow(formCard, gbc, row++, "Heart Rate", heartRateField);
        addFormRow(formCard, gbc, row++, "Total Cholesterol", totalCholesterolField);
        addFormRow(formCard, gbc, row++, "LDL", ldlField);
        addFormRow(formCard, gbc, row++, "HDL", hdlField);
        addFormRow(formCard, gbc, row++, "Triglycerides", triglyceridesField);
        addFormRow(formCard, gbc, row++, "Time of Day", timeOfDayField);
        addFormRow(formCard, gbc, row++, "Medication Timing", medTimingField);
        addFormRow(formCard, gbc, row++, "Activity Timing", activityTimingField);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionRow.setOpaque(false);
        JButton save = createAccentButton("Save Record");
        save.addActionListener(e -> handleSaveRecord());
        JButton clear = createSecondaryButton("Clear");
        clear.addActionListener(e -> clearAddRecordForm());
        actionRow.add(save);
        actionRow.add(clear);

        container.add(formCard, BorderLayout.CENTER);
        container.add(actionRow, BorderLayout.SOUTH);
        return container;
    }

    private JPanel buildHistoryPanel() {
        JPanel container = createCardContainer();
        container.setLayout(new BorderLayout(12, 12));

        JPanel controls = createPanelCard("Filter by Date Range (YYYY-MM-DD)");
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        styleTextField(startDateFilterField);
        styleTextField(endDateFilterField);
        controls.add(createMutedLabel("Start:"));
        controls.add(startDateFilterField);
        controls.add(createMutedLabel("End:"));
        controls.add(endDateFilterField);

        JButton apply = createAccentButton("Apply");
        apply.addActionListener(e -> handleApplyDateFilter());
        JButton clear = createSecondaryButton("Clear");
        clear.addActionListener(e -> handleClearDateFilter());
        JButton refresh = createSecondaryButton("Refresh");
        refresh.addActionListener(e -> refreshHistoryTable(recordManager.viewRecords()));
        controls.add(apply);
        controls.add(clear);
        controls.add(refresh);

        historyTable.setAutoCreateRowSorter(true);
        historyTable.setRowHeight(26);
        historyTable.setBackground(BG_PANEL_ALT);
        historyTable.setForeground(TEXT_PRIMARY);
        historyTable.setGridColor(new Color(55, 61, 75));
        historyTable.getTableHeader().setBackground(new Color(47, 54, 66));
        historyTable.getTableHeader().setForeground(TEXT_PRIMARY);
        historyTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane tableScroll = new JScrollPane(historyTable);
        tableScroll.getViewport().setBackground(BG_PANEL_ALT);
        tableScroll.setBorder(compoundCardBorder());

        container.add(controls, BorderLayout.NORTH);
        container.add(tableScroll, BorderLayout.CENTER);
        return container;
    }

    private JPanel buildAnalyticsPanel() {
        JPanel container = createCardContainer();
        container.setLayout(new BorderLayout(12, 12));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);
        JButton refresh = createAccentButton("Refresh Analytics");
        refresh.addActionListener(e -> refreshAnalyticsView());
        toolbar.add(refresh);

        analyticsTextArea.setEditable(false);
        analyticsTextArea.setBackground(BG_PANEL_ALT);
        analyticsTextArea.setForeground(TEXT_PRIMARY);
        analyticsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        analyticsTextArea.setLineWrap(true);
        analyticsTextArea.setWrapStyleWord(true);
        analyticsTextArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane areaScroll = new JScrollPane(analyticsTextArea);
        areaScroll.getViewport().setBackground(BG_PANEL_ALT);
        areaScroll.setBorder(compoundCardBorder());

        container.add(toolbar, BorderLayout.NORTH);
        container.add(areaScroll, BorderLayout.CENTER);
        return container;
    }

    private JPanel buildExportPanel() {
        JPanel container = createCardContainer();
        container.setLayout(new BorderLayout(12, 12));

        JPanel controls = createPanelCard("Export Data to CSV");
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        styleTextField(exportPathField);
        controls.add(createMutedLabel("Destination:"));
        controls.add(exportPathField);

        JButton browse = createSecondaryButton("Browse...");
        browse.addActionListener(e -> handleBrowseExportDestination());
        JButton export = createAccentButton("Export");
        export.addActionListener(e -> handleExportRecords());
        controls.add(browse);
        controls.add(export);

        JPanel noteCard = createPanelCard("Notes");
        noteCard.setLayout(new BorderLayout());
        JTextArea note = new JTextArea(
                "CSV export includes all metric fields, categories, session ID, timestamp, and contextual tags.\n"
                        + "If no records exist, an output file with header row is still generated.");
        note.setEditable(false);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        note.setBackground(BG_PANEL);
        note.setForeground(TEXT_MUTED);
        note.setFont(FONT_BODY);
        note.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        noteCard.add(note, BorderLayout.CENTER);

        container.add(controls, BorderLayout.NORTH);
        container.add(noteCard, BorderLayout.CENTER);
        return container;
    }

    private JButton createNavButton(String label, String card) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setFocusPainted(false);
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(new Color(40, 45, 56));
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.addActionListener(e -> {
            cardLayout.show(contentCards, card);
            setStatus("Viewing " + label + ".");
        });
        return button;
    }

    private JButton createAccentButton(String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT);
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return button;
    }

    private JButton createSecondaryButton(String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(new Color(58, 65, 80));
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return button;
    }

    private JPanel createCardContainer() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_APP);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        return panel;
    }

    private JPanel createPanelCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                compoundCardBorder(),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(),
                        title,
                        0,
                        0,
                        new Font("SansSerif", Font.BOLD, 13),
                        TEXT_MUTED)));
        return card;
    }

    private JPanel createInfoCard(String title, JLabel valueLabel) {
        JPanel card = createPanelCard(title);
        card.setLayout(new BorderLayout());
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStaticCard(String title, String text) {
        JPanel card = createPanelCard(title);
        card.setLayout(new BorderLayout());
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(BG_PANEL);
        area.setForeground(TEXT_MUTED);
        area.setFont(FONT_BODY);
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        card.add(area, BorderLayout.CENTER);
        return card;
    }

    private Border compoundCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(58, 64, 76)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    private JLabel createMutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_MUTED);
        return label;
    }

    private void addFormRow(JPanel formPanel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel rowLabel = new JLabel(label + ":");
        rowLabel.setForeground(TEXT_MUTED);
        rowLabel.setFont(FONT_BODY);
        formPanel.add(rowLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        styleTextField(field);
        formPanel.add(field, gbc);
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(BG_PANEL_ALT);
        textField.setForeground(TEXT_PRIMARY);
        textField.setCaretColor(TEXT_PRIMARY);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 76, 92)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    private void handleSaveRecord() {
        try {
            HealthRecord draft = new HealthRecord();
            draft.setSystolic(parseOptionalInteger("Systolic", systolicField.getText()));
            draft.setDiastolic(parseOptionalInteger("Diastolic", diastolicField.getText()));
            draft.setHeartRate(parseOptionalInteger("Heart Rate", heartRateField.getText()));
            draft.setTotalCholesterol(parseOptionalInteger("Total Cholesterol", totalCholesterolField.getText()));
            draft.setLdl(parseOptionalInteger("LDL", ldlField.getText()));
            draft.setHdl(parseOptionalInteger("HDL", hdlField.getText()));
            draft.setTriglycerides(parseOptionalInteger("Triglycerides", triglyceridesField.getText()));
            draft.setTimeOfDay(nullIfBlank(timeOfDayField.getText()));
            draft.setMedTiming(nullIfBlank(medTimingField.getText()));
            draft.setActivityTiming(nullIfBlank(activityTimingField.getText()));

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
            LocalDate startDate = parseDateField("Start Date", startDateFilterField.getText());
            LocalDate endDate = parseDateField("End Date", endDateFilterField.getText());

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
        List<HealthRecord> records = recordManager.viewRecords();
        refreshHistoryTable(records);
        refreshAnalyticsView();
        dashboardRecordCountLabel.setText(records.size() + " total record(s)");
    }

    private void refreshHistoryTable(List<HealthRecord> records) {
        historyTableModel.setRowCount(0);
        for (HealthRecord record : records) {
            historyTableModel.addRow(new Object[]{
                    valueOrBlank(record.getSessionId()),
                    DateTimeUtil.formatEpochMillis(record.getTimestampEpochMillis()),
                    valueOrBlank(record.getBloodPressureCategory()),
                    valueOrBlank(record.getSystolic()),
                    valueOrBlank(record.getDiastolic()),
                    valueOrBlank(record.getHeartRate()),
                    valueOrBlank(record.getTotalCholesterol()),
                    valueOrBlank(record.getLdl()),
                    valueOrBlank(record.getHdl()),
                    valueOrBlank(record.getTriglycerides()),
                    buildTagSummary(record),
                    valueOrBlank(record.getLipidSummary())
            });
        }
    }

    private void refreshAnalyticsView() {
        AnalyticsResult analytics = recordManager.viewAnalytics();
        StringBuilder builder = new StringBuilder();
        builder.append("Averages\n")
                .append("  Systolic: ").append(formatDouble(analytics.getAverageSystolic())).append("\n")
                .append("  Diastolic: ").append(formatDouble(analytics.getAverageDiastolic())).append("\n")
                .append("  Heart Rate: ").append(formatDouble(analytics.getAverageHeartRate())).append("\n")
                .append("  Total Cholesterol: ").append(formatDouble(analytics.getAverageTotalCholesterol())).append("\n")
                .append("  LDL: ").append(formatDouble(analytics.getAverageLdl())).append("\n")
                .append("  HDL: ").append(formatDouble(analytics.getAverageHdl())).append("\n")
                .append("  Triglycerides: ").append(formatDouble(analytics.getAverageTriglycerides())).append("\n\n")
                .append("Blood Pressure Category Counts\n");

        if (analytics.getBloodPressureCategoryCounts().isEmpty()) {
            builder.append("  No records.\n");
        } else {
            for (Map.Entry<String, Integer> entry : analytics.getBloodPressureCategoryCounts().entrySet()) {
                builder.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        builder.append("\nAlerts\n");
        for (String alert : analytics.getAlertSummaries()) {
            builder.append("  - ").append(alert).append("\n");
        }
        builder.append("\nTrend\n  ").append(valueOrBlank(analytics.getTrendSummary()));
        analyticsTextArea.setText(builder.toString());
        analyticsTextArea.setCaretPosition(0);
        dashboardTrendLabel.setText(valueOrBlank(analytics.getTrendSummary()));
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
            return "Recovered from backup";
        }
        if (initializationResult.isInitializedEmptyDataset()) {
            return "Initialized empty dataset";
        }
        return "Primary storage loaded";
    }

    private LocalDate parseDateField(String fieldName, String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required (format: YYYY-MM-DD).");
        }
        try {
            return LocalDate.parse(trimmed);
        } catch (Exception e) {
            throw new IllegalArgumentException(fieldName + " must use format YYYY-MM-DD.");
        }
    }

    private Integer parseOptionalInteger(String fieldName, String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be numeric.");
        }
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

    private String nullIfBlank(String value) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String buildTagSummary(HealthRecord record) {
        return "timeOfDay=" + valueOrBlank(record.getTimeOfDay())
                + ", medTiming=" + valueOrBlank(record.getMedTiming())
                + ", activityTiming=" + valueOrBlank(record.getActivityTiming());
    }

    private String formatDouble(Double value) {
        return value == null ? "N/A" : String.format("%.1f", value);
    }

    private String valueOrBlank(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}
