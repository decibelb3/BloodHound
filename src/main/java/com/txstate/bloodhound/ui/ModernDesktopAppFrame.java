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
import javax.swing.JComboBox;
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
import javax.swing.table.DefaultTableCellRenderer;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Modern themed Swing desktop UI variant for Bloodhound.
 */
public class ModernDesktopAppFrame extends JFrame {
    private static final int HISTORY_BP_CATEGORY_COLUMN = 2;
    private static final Color BG_APP = new Color(16, 18, 23);
    private static final Color BG_PANEL = new Color(28, 31, 39);
    private static final Color BG_PANEL_ALT = new Color(33, 36, 45);
    private static final Color BG_SIDEBAR = new Color(21, 24, 31);
    private static final Color ACCENT = new Color(69, 142, 255);
    private static final Color ACCENT_POSITIVE = new Color(120, 208, 143);
    private static final Color ACCENT_WARNING = new Color(255, 166, 77);
    private static final Color ACCENT_DANGER = new Color(255, 117, 117);
    private static final Color INPUT_BORDER = new Color(70, 76, 92);
    private static final Color INPUT_BORDER_INVALID = new Color(214, 97, 97);
    private static final Color INPUT_BG_INVALID = new Color(58, 38, 45);
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
    private final JLabel dashboardHighRiskLabel = new JLabel("0 high-risk sessions");
    private final JLabel dashboardLatestCategoryLabel = new JLabel("No readings yet");
    private final JLabel dashboardLatestVitalsLabel = new JLabel("Latest vitals: N/A");

    private final JTextField systolicField = new JTextField();
    private final JTextField diastolicField = new JTextField();
    private final JTextField heartRateField = new JTextField();
    private final JTextField totalCholesterolField = new JTextField();
    private final JTextField ldlField = new JTextField();
    private final JTextField hdlField = new JTextField();
    private final JTextField triglyceridesField = new JTextField();
    private final JComboBox<String> timeOfDayCombo = createTagDropdown("Morning", "Afternoon", "Evening");
    private final JComboBox<String> medTimingCombo = createTagDropdown(
            "Before Medication", "After Medication", "Not Applicable");
    private final JComboBox<String> activityTimingCombo = createTagDropdown(
            "Before Activity", "After Activity", "Resting");

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
    private final JLabel analyticsAvgSystolicLabel = new JLabel("N/A");
    private final JLabel analyticsAvgDiastolicLabel = new JLabel("N/A");
    private final JLabel analyticsAvgHeartRateLabel = new JLabel("N/A");
    private final JLabel analyticsAvgTotalCholesterolLabel = new JLabel("N/A");
    private final JLabel analyticsAvgLdlLabel = new JLabel("N/A");
    private final JLabel analyticsAvgHdlLabel = new JLabel("N/A");
    private final JLabel analyticsAvgTriglyceridesLabel = new JLabel("N/A");
    private final JLabel analyticsTrendSummaryLabel = new JLabel("No trend data yet");
    private final JTextArea analyticsCategoryCountsArea = new JTextArea();
    private final JTextArea analyticsAlertsArea = new JTextArea();
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
        container.setLayout(new BorderLayout(12, 12));

        container.add(createDashboardHeroCard(), BorderLayout.NORTH);

        JPanel metricGrid = new JPanel(new GridLayout(2, 3, 12, 12));
        metricGrid.setOpaque(false);
        metricGrid.add(createDashboardMetricCard("Total Records", "Records currently stored locally", dashboardRecordCountLabel));
        metricGrid.add(createDashboardMetricCard("Storage Health", "Startup and recovery state", dashboardStorageLabel));
        metricGrid.add(createDashboardMetricCard("Trend Snapshot", "Recent systolic trend summary", dashboardTrendLabel));
        metricGrid.add(createDashboardMetricCard("High-Risk Sessions", "Stage 2 or Crisis blood pressure", dashboardHighRiskLabel));
        metricGrid.add(createDashboardMetricCard("Latest BP Category", "Most recent classification", dashboardLatestCategoryLabel));
        metricGrid.add(createDashboardMetricCard("Latest Vitals", "Most recent blood pressure and heart rate", dashboardLatestVitalsLabel));

        container.add(metricGrid, BorderLayout.CENTER);
        container.add(createStaticCard(
                "Quick Actions",
                "Add a record to keep your timeline current, review trends in Analytics, and export CSV snapshots for class reporting."),
                BorderLayout.SOUTH);
        return container;
    }

    private JPanel buildAddRecordPanel() {
        JPanel container = createCardContainer();
        container.setLayout(new BorderLayout(12, 12));

        container.add(createStaticCard(
                "Record Entry Guidance",
                "Provide any measurements available for this session. Numeric fields are optional and "
                        + "validated against realistic ranges. Use context dropdowns when timing information is known."),
                BorderLayout.NORTH);

        JPanel sections = new JPanel(new GridLayout(1, 2, 12, 12));
        sections.setOpaque(false);

        JPanel metricsCard = createPanelCard("Vitals and Lipids");
        metricsCard.setLayout(new GridBagLayout());
        GridBagConstraints metricGbc = new GridBagConstraints();
        metricGbc.insets = new Insets(5, 5, 5, 5);
        metricGbc.fill = GridBagConstraints.HORIZONTAL;
        metricGbc.weightx = 1.0;

        int metricRow = 0;
        addFormRow(metricsCard, metricGbc, metricRow++, "Systolic (mmHg)", systolicField);
        addFormRow(metricsCard, metricGbc, metricRow++, "Diastolic (mmHg)", diastolicField);
        addFormRow(metricsCard, metricGbc, metricRow++, "Heart Rate (bpm)", heartRateField);
        addFormRow(metricsCard, metricGbc, metricRow++, "Total Cholesterol (mg/dL)", totalCholesterolField);
        addFormRow(metricsCard, metricGbc, metricRow++, "LDL (mg/dL)", ldlField);
        addFormRow(metricsCard, metricGbc, metricRow++, "HDL (mg/dL)", hdlField);
        addFormRow(metricsCard, metricGbc, metricRow++, "Triglycerides (mg/dL)", triglyceridesField);

        JPanel contextCard = createPanelCard("Context Tags");
        contextCard.setLayout(new GridBagLayout());
        GridBagConstraints contextGbc = new GridBagConstraints();
        contextGbc.insets = new Insets(5, 5, 5, 5);
        contextGbc.fill = GridBagConstraints.HORIZONTAL;
        contextGbc.weightx = 1.0;

        int contextRow = 0;
        addFormComboRow(contextCard, contextGbc, contextRow++, "Time of Day", timeOfDayCombo);
        addFormComboRow(contextCard, contextGbc, contextRow++, "Medication Timing", medTimingCombo);
        addFormComboRow(contextCard, contextGbc, contextRow++, "Activity Timing", activityTimingCombo);

        JLabel contextHint = createMutedLabel("Tip: choose \"Not Specified\" when timing is unknown.");
        contextHint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        contextGbc.gridx = 0;
        contextGbc.gridy = contextRow;
        contextGbc.gridwidth = 2;
        contextGbc.weightx = 1.0;
        contextCard.add(contextHint, contextGbc);

        sections.add(metricsCard);
        sections.add(contextCard);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionRow.setOpaque(false);
        JButton save = createAccentButton("Save Record");
        save.addActionListener(e -> handleSaveRecord());
        JButton clear = createSecondaryButton("Clear");
        clear.addActionListener(e -> clearAddRecordForm());
        actionRow.add(save);
        actionRow.add(clear);
        actionRow.add(createMutedLabel("At least one metric is required to save."));

        container.add(sections, BorderLayout.CENTER);
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
        historyTable.getColumnModel().getColumn(HISTORY_BP_CATEGORY_COLUMN)
                .setCellRenderer(new BloodPressureCategoryCellRenderer());

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
        toolbar.add(createMutedLabel("Snapshot refreshes automatically after saving records."));

        JPanel keyMetrics = new JPanel(new GridLayout(1, 4, 12, 12));
        keyMetrics.setOpaque(false);
        keyMetrics.add(createAnalyticsMetricCard("Avg Systolic", "mmHg", analyticsAvgSystolicLabel));
        keyMetrics.add(createAnalyticsMetricCard("Avg Diastolic", "mmHg", analyticsAvgDiastolicLabel));
        keyMetrics.add(createAnalyticsMetricCard("Avg Heart Rate", "bpm", analyticsAvgHeartRateLabel));
        keyMetrics.add(createAnalyticsMetricCard("Avg Total Cholesterol", "mg/dL", analyticsAvgTotalCholesterolLabel));

        JPanel middleRow = new JPanel(new GridLayout(1, 2, 12, 12));
        middleRow.setOpaque(false);
        middleRow.add(createAnalyticsLipidCard());
        middleRow.add(createAnalyticsTrendCard());

        styleAnalyticsArea(analyticsCategoryCountsArea);
        styleAnalyticsArea(analyticsAlertsArea);

        JPanel categoryCard = createPanelCard("Blood Pressure Category Counts");
        categoryCard.setLayout(new BorderLayout());
        JScrollPane categoryScroll = new JScrollPane(analyticsCategoryCountsArea);
        categoryScroll.getViewport().setBackground(BG_PANEL_ALT);
        categoryScroll.setBorder(BorderFactory.createEmptyBorder());
        categoryCard.add(categoryScroll, BorderLayout.CENTER);

        JPanel alertsCard = createPanelCard("Alert Summaries");
        alertsCard.setLayout(new BorderLayout());
        JScrollPane alertsScroll = new JScrollPane(analyticsAlertsArea);
        alertsScroll.getViewport().setBackground(BG_PANEL_ALT);
        alertsScroll.setBorder(BorderFactory.createEmptyBorder());
        alertsCard.add(alertsScroll, BorderLayout.CENTER);

        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 12, 12));
        bottomRow.setOpaque(false);
        bottomRow.add(categoryCard);
        bottomRow.add(alertsCard);

        JPanel analyticsDashboard = new JPanel(new BorderLayout(12, 12));
        analyticsDashboard.setOpaque(false);
        analyticsDashboard.add(keyMetrics, BorderLayout.NORTH);
        analyticsDashboard.add(middleRow, BorderLayout.CENTER);
        analyticsDashboard.add(bottomRow, BorderLayout.SOUTH);

        container.add(toolbar, BorderLayout.NORTH);
        container.add(analyticsDashboard, BorderLayout.CENTER);
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

    private JPanel createDashboardHeroCard() {
        JPanel card = createPanelCard("Health Overview");
        card.setLayout(new BorderLayout(10, 10));

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Your Offline Health Snapshot");
        heading.setForeground(TEXT_PRIMARY);
        heading.setFont(new Font("SansSerif", Font.BOLD, 19));
        JLabel summary = new JLabel("<html><div style='width:760px'>Track blood pressure and lipid trends over time. "
                + "All information remains local to this device and updates after every saved record.</div></html>");
        summary.setForeground(TEXT_MUTED);
        summary.setFont(FONT_BODY);

        textBlock.add(heading);
        textBlock.add(Box.createVerticalStrut(6));
        textBlock.add(summary);

        card.add(textBlock, BorderLayout.CENTER);
        return card;
    }

    private JPanel createDashboardMetricCard(String title, String subtitle, JLabel valueLabel) {
        JPanel card = createPanelCard(title);
        card.setLayout(new BorderLayout(0, 8));

        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 0, 12));

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setForeground(TEXT_MUTED);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 10, 12));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subtitleLabel, BorderLayout.SOUTH);
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

    private JPanel createAnalyticsMetricCard(String title, String unit, JLabel valueLabel) {
        JPanel card = createPanelCard(title);
        card.setLayout(new BorderLayout(0, 6));

        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        JLabel unitLabel = new JLabel(unit);
        unitLabel.setForeground(TEXT_MUTED);
        unitLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        unitLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 10, 12));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(unitLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createAnalyticsLipidCard() {
        JPanel card = createPanelCard("Lipid Averages");
        card.setLayout(new GridLayout(3, 1, 0, 6));
        card.add(createAnalyticsLine("LDL", analyticsAvgLdlLabel, "mg/dL"));
        card.add(createAnalyticsLine("HDL", analyticsAvgHdlLabel, "mg/dL"));
        card.add(createAnalyticsLine("Triglycerides", analyticsAvgTriglyceridesLabel, "mg/dL"));
        return card;
    }

    private JPanel createAnalyticsTrendCard() {
        JPanel card = createPanelCard("Trend Summary");
        card.setLayout(new BorderLayout(0, 8));

        analyticsTrendSummaryLabel.setForeground(TEXT_PRIMARY);
        analyticsTrendSummaryLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        analyticsTrendSummaryLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        JLabel hint = createMutedLabel("Trend compares recent systolic readings against earlier sessions.");
        hint.setBorder(BorderFactory.createEmptyBorder(0, 12, 10, 12));

        card.add(analyticsTrendSummaryLabel, BorderLayout.CENTER);
        card.add(hint, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createAnalyticsLine(String metricName, JLabel valueLabel, String unit) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel nameLabel = new JLabel(metricName);
        nameLabel.setForeground(TEXT_MUTED);
        nameLabel.setFont(FONT_BODY);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 6));

        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        valueLabel.setHorizontalAlignment(JLabel.RIGHT);

        JLabel unitLabel = new JLabel(unit);
        unitLabel.setForeground(TEXT_MUTED);
        unitLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        unitLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 12));

        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setOpaque(false);
        valuePanel.add(valueLabel, BorderLayout.CENTER);
        valuePanel.add(unitLabel, BorderLayout.EAST);

        row.add(nameLabel, BorderLayout.WEST);
        row.add(valuePanel, BorderLayout.CENTER);
        return row;
    }

    private void styleAnalyticsArea(JTextArea area) {
        area.setEditable(false);
        area.setBackground(BG_PANEL_ALT);
        area.setForeground(TEXT_PRIMARY);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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

    private void addFormComboRow(JPanel formPanel, GridBagConstraints gbc, int row, String label, JComboBox<String> comboBox) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.gridwidth = 1;
        JLabel rowLabel = new JLabel(label + ":");
        rowLabel.setForeground(TEXT_MUTED);
        rowLabel.setFont(FONT_BODY);
        formPanel.add(rowLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        styleComboBox(comboBox);
        formPanel.add(comboBox, gbc);
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(BG_PANEL_ALT);
        textField.setForeground(TEXT_PRIMARY);
        textField.setCaretColor(TEXT_PRIMARY);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        textField.setToolTipText(null);
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(BG_PANEL_ALT);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setFont(FONT_BODY);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
    }

    private JComboBox<String> createTagDropdown(String... options) {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("Not Specified");
        if (options != null) {
            for (String option : options) {
                comboBox.addItem(option);
            }
        }
        return comboBox;
    }

    private void handleSaveRecord() {
        resetAddRecordInputStyles();

        List<String> inputErrors = new ArrayList<>();
        HealthRecord draft = new HealthRecord();
        draft.setSystolic(parseOptionalIntegerForInput("Systolic", systolicField, inputErrors));
        draft.setDiastolic(parseOptionalIntegerForInput("Diastolic", diastolicField, inputErrors));
        draft.setHeartRate(parseOptionalIntegerForInput("Heart Rate", heartRateField, inputErrors));
        draft.setTotalCholesterol(parseOptionalIntegerForInput("Total Cholesterol", totalCholesterolField, inputErrors));
        draft.setLdl(parseOptionalIntegerForInput("LDL", ldlField, inputErrors));
        draft.setHdl(parseOptionalIntegerForInput("HDL", hdlField, inputErrors));
        draft.setTriglycerides(parseOptionalIntegerForInput("Triglycerides", triglyceridesField, inputErrors));
        draft.setTimeOfDay(selectedDropdownValue(timeOfDayCombo));
        draft.setMedTiming(selectedDropdownValue(medTimingCombo));
        draft.setActivityTiming(selectedDropdownValue(activityTimingCombo));

        if (!inputErrors.isEmpty()) {
            showValidationIssues(
                    "Some fields use invalid input format.",
                    inputErrors,
                    "Please correct highlighted fields and try again.");
            setStatus("Save failed. Fix highlighted numeric fields.");
            return;
        }

        OperationResult<AddRecordResponse> result = recordManager.addRecord(draft);
        if (!result.isSuccess()) {
            highlightFieldsFromValidationErrors(result.getErrors());
            showValidationIssues(result.getMessage(), result.getErrors(), "Review highlighted fields and save again.");
            setStatus("Save failed. Validation requirements were not met.");
            return;
        }

        AddRecordResponse payload = result.getData();
        showSaveConfirmation(payload, result.getMessage());
        clearAddRecordForm();
        refreshAllViews();
        if (payload.getAlerts().isEmpty()) {
            setStatus("Record saved at " + DateTimeUtil.formatEpochMillis(payload.getRecord().getTimestampEpochMillis()));
        } else {
            setStatus("Record saved with " + payload.getAlerts().size() + " risk alert(s).");
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
        List<HealthRecord> records = recordManager.viewRecords();
        refreshHistoryTable(records);
        refreshAnalyticsView();
        refreshDashboardSummary(records);
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
        analyticsAvgSystolicLabel.setText(UiRecordHelper.formatDouble(analytics.getAverageSystolic()));
        analyticsAvgDiastolicLabel.setText(UiRecordHelper.formatDouble(analytics.getAverageDiastolic()));
        analyticsAvgHeartRateLabel.setText(UiRecordHelper.formatDouble(analytics.getAverageHeartRate()));
        analyticsAvgTotalCholesterolLabel.setText(UiRecordHelper.formatDouble(analytics.getAverageTotalCholesterol()));
        analyticsAvgLdlLabel.setText(UiRecordHelper.formatDouble(analytics.getAverageLdl()));
        analyticsAvgHdlLabel.setText(UiRecordHelper.formatDouble(analytics.getAverageHdl()));
        analyticsAvgTriglyceridesLabel.setText(UiRecordHelper.formatDouble(analytics.getAverageTriglycerides()));

        analyticsCategoryCountsArea.setText(buildCategoryCountsSummary(analytics.getBloodPressureCategoryCounts()));
        analyticsCategoryCountsArea.setCaretPosition(0);
        analyticsAlertsArea.setText(buildAlertSummary(analytics.getAlertSummaries()));
        analyticsAlertsArea.setCaretPosition(0);

        analyticsTrendSummaryLabel.setText(valueOrFallback(analytics.getTrendSummary(), "No trend data yet"));
        analyticsTrendSummaryLabel.setForeground(colorForTrend(analytics.getTrendSummary()));
        dashboardTrendLabel.setText(valueOrFallback(analytics.getTrendSummary(), "No trend data yet"));
        dashboardTrendLabel.setForeground(colorForTrend(analytics.getTrendSummary()));
    }

    private String buildCategoryCountsSummary(Map<String, Integer> counts) {
        if (counts == null || counts.isEmpty()) {
            return "No records.";
        }
        StringBuilder builder = new StringBuilder();
        String[] preferredOrder = {"Crisis", "Stage 2", "Stage 1", "Elevated", "Normal", "Not Provided", "Unclassified"};
        Set<String> consumed = new HashSet<>();
        for (String category : preferredOrder) {
            Integer count = counts.get(category);
            if (count != null) {
                builder.append(category).append(": ").append(count).append("\n");
                consumed.add(category);
            }
        }
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (!consumed.contains(entry.getKey())) {
                builder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        return builder.toString().trim();
    }

    private String buildAlertSummary(List<String> alerts) {
        if (alerts == null || alerts.isEmpty()) {
            return "No alerts available.";
        }
        StringBuilder builder = new StringBuilder();
        for (String alert : alerts) {
            builder.append("- ").append(alert).append("\n");
        }
        return builder.toString().trim();
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

    private void refreshDashboardSummary(List<HealthRecord> records) {
        dashboardRecordCountLabel.setText(records.size() + " record(s)");
        dashboardStorageLabel.setForeground(TEXT_PRIMARY);
        dashboardStorageLabel.setText(buildStorageStatusText());

        int highRiskCount = countHighRiskRecords(records);
        dashboardHighRiskLabel.setText(highRiskCount + (highRiskCount == 1 ? " high-risk session" : " high-risk sessions"));
        dashboardHighRiskLabel.setForeground(highRiskCount == 0 ? ACCENT_POSITIVE : ACCENT_WARNING);

        if (records.isEmpty()) {
            dashboardLatestCategoryLabel.setText("No readings yet");
            applyCategoryBadgeStyle(dashboardLatestCategoryLabel, "Not Provided");
            dashboardLatestCategoryLabel.setText("No readings yet");
            dashboardLatestVitalsLabel.setText("Latest vitals: N/A");
            dashboardLatestVitalsLabel.setForeground(TEXT_MUTED);
            return;
        }

        HealthRecord latest = records.get(0);
        String category = valueOrFallback(latest.getBloodPressureCategory(), "Not provided");
        dashboardLatestCategoryLabel.setText(category);
        applyCategoryBadgeStyle(dashboardLatestCategoryLabel, category);
        dashboardLatestVitalsLabel.setText(buildLatestVitalsSummary(latest));
        dashboardLatestVitalsLabel.setForeground(TEXT_PRIMARY);
    }

    private int countHighRiskRecords(List<HealthRecord> records) {
        int count = 0;
        for (HealthRecord record : records) {
            String category = record.getBloodPressureCategory();
            if ("Stage 2".equals(category) || "Crisis".equals(category)) {
                count++;
            }
        }
        return count;
    }

    private String buildLatestVitalsSummary(HealthRecord record) {
        String bp = valueOrDash(record.getSystolic()) + "/" + valueOrDash(record.getDiastolic()) + " mmHg";
        String heartRate = "HR " + valueOrDash(record.getHeartRate()) + " bpm";
        return bp + "  |  " + heartRate;
    }

    private void applyCategoryBadgeStyle(JLabel label, String category) {
        CategoryVisual visual = categoryVisual(category);
        label.setForeground(visual.textColor);
        label.setBackground(visual.badgeBackgroundColor);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(visual.borderColor),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    private CategoryVisual categoryVisual(String rawCategory) {
        String category = rawCategory == null ? "" : rawCategory.trim();
        switch (category) {
            case "Normal":
                return new CategoryVisual(
                        new Color(132, 220, 161),
                        new Color(36, 59, 46),
                        new Color(71, 126, 96),
                        new Color(44, 65, 54));
            case "Elevated":
                return new CategoryVisual(
                        new Color(255, 214, 130),
                        new Color(63, 52, 32),
                        new Color(126, 101, 51),
                        new Color(68, 56, 36));
            case "Stage 1":
                return new CategoryVisual(
                        new Color(255, 184, 117),
                        new Color(74, 49, 35),
                        new Color(137, 85, 50),
                        new Color(79, 54, 41));
            case "Stage 2":
                return new CategoryVisual(
                        new Color(255, 153, 153),
                        new Color(78, 41, 44),
                        new Color(151, 74, 84),
                        new Color(83, 45, 49));
            case "Crisis":
                return new CategoryVisual(
                        new Color(255, 116, 116),
                        new Color(83, 32, 37),
                        new Color(174, 67, 78),
                        new Color(88, 38, 43));
            default:
                return new CategoryVisual(
                        TEXT_PRIMARY,
                        new Color(47, 52, 62),
                        new Color(78, 86, 101),
                        BG_PANEL_ALT);
        }
    }

    private Color colorForTrend(String trendSummary) {
        if (trendSummary == null) {
            return TEXT_PRIMARY;
        }
        String lower = trendSummary.toLowerCase();
        if (lower.contains("improving")) {
            return ACCENT_POSITIVE;
        }
        if (lower.contains("rising")) {
            return ACCENT_WARNING;
        }
        return TEXT_PRIMARY;
    }

    private Integer parseOptionalIntegerForInput(String fieldName, JTextField field, List<String> inputErrors) {
        String value = field.getText();
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            String error = fieldName + " must be numeric.";
            inputErrors.add(error);
            markFieldInvalid(field, error);
            return null;
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
        timeOfDayCombo.setSelectedIndex(0);
        medTimingCombo.setSelectedIndex(0);
        activityTimingCombo.setSelectedIndex(0);
        resetAddRecordInputStyles();
    }

    private String selectedDropdownValue(JComboBox<String> comboBox) {
        Object selected = comboBox.getSelectedItem();
        if (selected == null) {
            return null;
        }
        String value = selected.toString().trim();
        return "Not Specified".equals(value) || value.isBlank() ? null : value;
    }

    private void resetAddRecordInputStyles() {
        styleTextField(systolicField);
        styleTextField(diastolicField);
        styleTextField(heartRateField);
        styleTextField(totalCholesterolField);
        styleTextField(ldlField);
        styleTextField(hdlField);
        styleTextField(triglyceridesField);
    }

    private void markFieldInvalid(JTextField field, String tooltip) {
        field.setBackground(INPUT_BG_INVALID);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER_INVALID),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        field.setToolTipText(tooltip);
    }

    private void highlightFieldsFromValidationErrors(List<String> errors) {
        if (errors == null) {
            return;
        }
        for (String error : errors) {
            String normalized = error == null ? "" : error.toLowerCase();
            if (normalized.contains("at least one health metric")) {
                markFieldInvalid(systolicField, "Provide at least one metric value.");
                markFieldInvalid(diastolicField, "Provide at least one metric value.");
                markFieldInvalid(heartRateField, "Provide at least one metric value.");
                markFieldInvalid(totalCholesterolField, "Provide at least one metric value.");
                markFieldInvalid(ldlField, "Provide at least one metric value.");
                markFieldInvalid(hdlField, "Provide at least one metric value.");
                markFieldInvalid(triglyceridesField, "Provide at least one metric value.");
            }
            if (normalized.contains("systolic")) {
                markFieldInvalid(systolicField, error);
            }
            if (normalized.contains("diastolic")) {
                markFieldInvalid(diastolicField, error);
            }
            if (normalized.contains("heart rate")) {
                markFieldInvalid(heartRateField, error);
            }
            if (normalized.contains("total cholesterol")) {
                markFieldInvalid(totalCholesterolField, error);
            }
            if (normalized.contains("ldl")) {
                markFieldInvalid(ldlField, error);
            }
            if (normalized.contains("hdl")) {
                markFieldInvalid(hdlField, error);
            }
            if (normalized.contains("triglycerides")) {
                markFieldInvalid(triglyceridesField, error);
            }
        }
    }

    private void showValidationIssues(String titleLine, List<String> errors, String footerLine) {
        StringBuilder message = new StringBuilder(titleLine).append("\n");
        if (errors != null && !errors.isEmpty()) {
            message.append("\nDetails:\n");
            for (String error : errors) {
                message.append(" - ").append(error).append("\n");
            }
        }
        message.append("\n").append(footerLine);
        JOptionPane.showMessageDialog(this, message.toString(), "Validation Issue", JOptionPane.WARNING_MESSAGE);
    }

    private void showSaveConfirmation(AddRecordResponse payload, String baseMessage) {
        HealthRecord saved = payload.getRecord();
        List<String> alerts = payload.getAlerts();

        StringBuilder message = new StringBuilder(baseMessage)
                .append("\nSession ID: ").append(saved.getSessionId())
                .append("\nRecorded: ").append(DateTimeUtil.formatEpochMillis(saved.getTimestampEpochMillis()))
                .append("\nBlood Pressure Category: ").append(valueOrFallback(saved.getBloodPressureCategory(), "Not provided"))
                .append("\nLipid Summary: ").append(valueOrFallback(saved.getLipidSummary(), "Not provided"));

        int dialogType = JOptionPane.INFORMATION_MESSAGE;
        String dialogTitle = "Record Saved";
        if (alerts.isEmpty()) {
            message.append("\n\nNo risk alerts were detected for this entry.");
        } else {
            dialogType = JOptionPane.WARNING_MESSAGE;
            dialogTitle = "Record Saved - Risk Alerts";
            message.append("\n\nRisk Alerts (Review):\n - ").append(String.join("\n - ", alerts));
        }

        JOptionPane.showMessageDialog(this, message.toString(), dialogTitle, dialogType);
    }

    private String valueOrDash(Object value) {
        return value == null ? "-" : String.valueOf(value);
    }

    private String valueOrFallback(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private static final class CategoryVisual {
        private final Color textColor;
        private final Color badgeBackgroundColor;
        private final Color borderColor;
        private final Color tableCellBackgroundColor;

        private CategoryVisual(Color textColor, Color badgeBackgroundColor, Color borderColor, Color tableCellBackgroundColor) {
            this.textColor = textColor;
            this.badgeBackgroundColor = badgeBackgroundColor;
            this.borderColor = borderColor;
            this.tableCellBackgroundColor = tableCellBackgroundColor;
        }
    }

    private final class BloodPressureCategoryCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel component = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String category = valueOrFallback(value == null ? null : value.toString(), "Unclassified");
            CategoryVisual visual = categoryVisual(category);

            component.setText(category);
            component.setHorizontalAlignment(JLabel.CENTER);
            component.setOpaque(true);
            if (isSelected) {
                component.setForeground(table.getSelectionForeground());
                component.setBackground(table.getSelectionBackground());
                component.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            } else {
                component.setForeground(visual.textColor);
                component.setBackground(visual.tableCellBackgroundColor);
                component.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(visual.borderColor),
                        BorderFactory.createEmptyBorder(2, 6, 2, 6)));
            }
            return component;
        }
    }
}
