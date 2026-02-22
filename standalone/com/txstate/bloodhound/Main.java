package com.txstate.bloodhound;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

/**
 * Bloodhound - Standalone health tracker (Swing GUI).
 */
public class Main {

    private static final Color BG_DARK = new Color(30, 41, 59);
    private static final Color BG_CARD = new Color(51, 65, 85);
    private static final Color ACCENT = new Color(239, 68, 68);
    private static final Color ACCENT_HOVER = new Color(248, 113, 113);
    private static final Color TEXT_LIGHT = new Color(248, 250, 252);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private DatabaseControllerDesktop db;
    private JTextArea readingsArea;
    private JTextArea analyticsArea;
    private BarChartPanel bpChartPanel;
    private BarChartPanel ldlChartPanel;
    private BarChartPanel hdlChartPanel;
    private JLabel statusLabel;
    private JComboBox<String> categoryFilter;
    private JComboBox<String> windowFilter;
    private JTextField fromDateField;
    private JTextField toDateField;
    private JTextField sessionIdField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().run());
    }

    private void run() {
        db = new DatabaseControllerDesktop();
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(BG_DARK);

        frame = new JFrame("Bloodhound – Blood Pressure Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 550);
        frame.setMinimumSize(new Dimension(560, 450));
        frame.setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(BG_DARK);
        main.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Header with nav buttons
        JPanel header = buildHeader();
        main.add(header, BorderLayout.NORTH);

        main.add(cardPanel, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(TEXT_MUTED);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setBorder(new EmptyBorder(8, 24, 12, 24));
        main.add(statusLabel, BorderLayout.SOUTH);

        // Cards
        cardPanel.add(buildAddPanel(), "add");
        cardPanel.add(buildReadingsPanel(), "readings");
        cardPanel.add(buildAnalyticsPanel(), "analytics");
        cardPanel.add(buildExportPanel(), "export");

        frame.setContentPane(main);
        frame.setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        header.setBackground(BG_DARK);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("Bloodhound");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_LIGHT);
        header.add(title);

        header.add(Box.createHorizontalStrut(24));

        for (NavButton nb : new NavButton[]{
                new NavButton("Add Reading", "add"),
                new NavButton("View Readings", "readings"),
                new NavButton("Analytics", "analytics"),
                new NavButton("Export CSV", "export")
        }) {
            JButton btn = createNavButton(nb.label);
            btn.addActionListener(e -> {
                cardLayout.show(cardPanel, nb.card);
                statusLabel.setText(nb.label);
                if ("readings".equals(nb.card)) refreshReadings();
                if ("analytics".equals(nb.card)) refreshAnalytics();
            });
            header.add(btn);
        }

        return header;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXT_LIGHT);
        btn.setBackground(BG_CARD);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BG_CARD, 1),
                new EmptyBorder(10, 18, 10, 18)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(BG_CARD);
            }
        });
        return btn;
    }

    private JPanel buildAddPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(24, 48, 48, 48));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Add Health Reading");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(TEXT_LIGHT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(24));

        JTextField sysField = new JTextField(8);
        JTextField diaField = new JTextField(8);
        JTextField hrField = new JTextField(8);
        JTextField tcField = new JTextField(8);
        JTextField ldlField = new JTextField(8);
        JTextField hdlField = new JTextField(8);
        JComboBox<String> timeCombo = new JComboBox<>(new String[]{"Morning", "Afternoon", "Evening", "Night"});
        JTextField medField = new JTextField(20);
        JTextField activityField = new JTextField(20);

        panel.add(buildFormRow("Systolic (mmHg)", sysField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildFormRow("Diastolic (mmHg)", diaField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildFormRow("Heart Rate (optional)", hrField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildFormRow("Total Cholesterol (mg/dL)", tcField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildFormRow("LDL (mg/dL)", ldlField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildFormRow("HDL (mg/dL)", hdlField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildFormRow("Time of Day", timeCombo));
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildFormRow("Med Timing", medField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildFormRow("Activity", activityField));
        panel.add(Box.createVerticalStrut(28));

        JButton saveBtn = new JButton("Save Reading");
        stylePrimaryButton(saveBtn);
        saveBtn.addActionListener(e -> {
            try {
                int systolic = Integer.parseInt(sysField.getText().trim());
                int diastolic = Integer.parseInt(diaField.getText().trim());
                int heartRate = 0;
                String hr = hrField.getText().trim();
                if (!hr.isEmpty()) heartRate = Integer.parseInt(hr);
                int totalCholesterol = parseOptionalPositiveInt(tcField.getText().trim());
                int ldl = parseOptionalPositiveInt(ldlField.getText().trim());
                int hdl = parseOptionalPositiveInt(hdlField.getText().trim());

                BPRecord record = new BPRecord(
                        UUID.randomUUID().toString(),
                        System.currentTimeMillis(),
                        systolic, diastolic, heartRate,
                        (String) timeCombo.getSelectedItem(),
                        medField.getText().trim().isEmpty() ? "N/A" : medField.getText().trim(),
                        activityField.getText().trim().isEmpty() ? "N/A" : activityField.getText().trim(),
                        totalCholesterol,
                        ldl,
                        hdl);

                db.insertRecord(record);
                sysField.setText("");
                diaField.setText("");
                hrField.setText("");
                tcField.setText("");
                ldlField.setText("");
                hdlField.setText("");
                medField.setText("");
                activityField.setText("");
                statusLabel.setText("Saved Risk Level: " + record.getAHACategory());
                JOptionPane.showMessageDialog(frame, "Saved. Blood Pressure Risk Level: " + record.getAHACategory(),
                        "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Enter valid positive numbers for all numeric fields.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(saveBtn);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel buildFormRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(BG_DARK);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(500, 40));

        JLabel lbl = new JLabel(labelText);
        lbl.setPreferredSize(new Dimension(160, 28));
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        row.add(lbl);

        field.setPreferredSize(new Dimension(200, 28));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (field instanceof JTextField) {
            ((JTextField) field).setCaretColor(TEXT_LIGHT);
            field.setBackground(BG_CARD);
            field.setForeground(TEXT_LIGHT);
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                    new EmptyBorder(4, 8, 4, 8)));
        }
        if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setBackground(BG_CARD);
            ((JComboBox<?>) field).setForeground(TEXT_LIGHT);
        }
        row.add(field);
        return row;
    }

    private JPanel buildReadingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(24, 48, 48, 48));

        JLabel heading = new JLabel("Recent Readings");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(TEXT_LIGHT);
        heading.setBorder(new EmptyBorder(0, 0, 16, 0));
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG_DARK);
        top.add(heading, BorderLayout.WEST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filters.setBackground(BG_DARK);
        filters.add(newLabel("Risk Level"));
        categoryFilter = new JComboBox<>(new String[]{"All", "Normal", "Elevated", "Stage 1", "Stage 2", "Crisis"});
        styleCombo(categoryFilter);
        filters.add(categoryFilter);
        filters.add(newLabel("From"));
        fromDateField = new JTextField(8);
        styleTextField(fromDateField);
        fromDateField.setToolTipText("yyyy-mm-dd");
        filters.add(fromDateField);
        filters.add(newLabel("To"));
        toDateField = new JTextField(8);
        styleTextField(toDateField);
        toDateField.setToolTipText("yyyy-mm-dd");
        filters.add(toDateField);
        JButton applyBtn = new JButton("Apply");
        stylePrimaryButton(applyBtn);
        applyBtn.addActionListener(e -> refreshReadings());
        filters.add(applyBtn);
        top.add(filters, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        readingsArea = new JTextArea(12, 40);
        readingsArea.setEditable(false);
        readingsArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        readingsArea.setBackground(BG_CARD);
        readingsArea.setForeground(TEXT_LIGHT);
        readingsArea.setCaretColor(TEXT_LIGHT);
        readingsArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        readingsArea.setMargin(new Insets(12, 12, 12, 12));

        JScrollPane scroll = new JScrollPane(readingsArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(71, 85, 105), 1));
        scroll.getViewport().setBackground(BG_CARD);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actions.setBackground(BG_DARK);
        actions.add(newLabel("Session ID"));
        sessionIdField = new JTextField(26);
        styleTextField(sessionIdField);
        actions.add(sessionIdField);
        JButton editBtn = new JButton("Edit");
        stylePrimaryButton(editBtn);
        editBtn.addActionListener(e -> editBySessionId());
        actions.add(editBtn);
        JButton deleteBtn = new JButton("Delete");
        stylePrimaryButton(deleteBtn);
        deleteBtn.addActionListener(e -> deleteBySessionId());
        actions.add(deleteBtn);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshReadings() {
        List<BPRecord> records = getFilteredReadings();
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (records.isEmpty()) {
            sb.append("No readings yet. Add one from the Add Reading tab.");
        } else {
            sb.append(String.format("%-8s %-16s %-9s %-10s %-8s %-7s %-7s %-12s%n",
                    "ID", "Timestamp", "BP", "RiskLvl", "TC", "LDL", "HDL", "Time"));
            sb.append("-------------------------------------------------------------------------------------\n");
            for (BPRecord r : records) {
                sb.append(String.format("%-8s %-16s %-9s %-10s %-8s %-7s %-7s %-12s%n",
                        shortId(r.getSessionId()),
                        fmt.format(new Date(r.getTimestamp())),
                        r.getSystolic() + "/" + r.getDiastolic(),
                        r.getAHACategory(),
                        nonZeroOrDash(r.getTotalCholesterol()),
                        nonZeroOrDash(r.getLdl()),
                        nonZeroOrDash(r.getHdl()),
                        r.getTimeOfDay()));
            }
        }
        readingsArea.setText(sb.toString());
    }

    private JPanel buildAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(24, 48, 48, 48));

        JLabel heading = new JLabel("Analytics");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(TEXT_LIGHT);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setBackground(BG_DARK);
        top.add(heading);
        top.add(Box.createHorizontalStrut(14));
        top.add(newLabel("Window"));
        windowFilter = new JComboBox<>(new String[]{"All", "7 Days", "30 Days", "90 Days"});
        styleCombo(windowFilter);
        top.add(windowFilter);
        JButton refreshBtn = new JButton("Refresh");
        stylePrimaryButton(refreshBtn);
        refreshBtn.addActionListener(e -> refreshAnalytics());
        top.add(refreshBtn);
        panel.add(top, BorderLayout.NORTH);

        analyticsArea = new JTextArea();
        analyticsArea.setEditable(false);
        analyticsArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        analyticsArea.setBackground(BG_CARD);
        analyticsArea.setForeground(TEXT_LIGHT);
        analyticsArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        JScrollPane scroll = new JScrollPane(analyticsArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(71, 85, 105), 1));

        JPanel charts = new JPanel(new GridLayout(3, 1, 0, 8));
        charts.setBackground(BG_DARK);
        bpChartPanel = new BarChartPanel("BP Progression (Systolic/Diastolic)", "Systolic", "Diastolic");
        ldlChartPanel = new BarChartPanel("LDL Progression (mg/dL)", "LDL", null);
        hdlChartPanel = new BarChartPanel("HDL Progression (mg/dL)", "HDL", null);
        charts.add(bpChartPanel);
        charts.add(ldlChartPanel);
        charts.add(hdlChartPanel);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setBackground(BG_DARK);
        scroll.setPreferredSize(new Dimension(100, 170));
        center.add(scroll, BorderLayout.NORTH);
        center.add(charts, BorderLayout.CENTER);

        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    private void refreshAnalytics() {
        List<BPRecord> records = filterByWindow(db.getAllRecords(), (String) windowFilter.getSelectedItem());
        if (records.isEmpty()) {
            analyticsArea.setText("No records in selected window.");
            bpChartPanel.setSeries(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            ldlChartPanel.setSeries(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            hdlChartPanel.setSeries(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            return;
        }

        int totalSys = 0;
        int totalDia = 0;
        int totalHr = 0;
        int totalTc = 0;
        int totalLdl = 0;
        int totalHdl = 0;
        int hrCount = 0;
        int tcCount = 0;
        int ldlCount = 0;
        int hdlCount = 0;
        int normal = 0, elevated = 0, stage1 = 0, stage2 = 0, crisis = 0;

        for (BPRecord r : records) {
            totalSys += r.getSystolic();
            totalDia += r.getDiastolic();
            if (r.getHeartRate() > 0) {
                totalHr += r.getHeartRate();
                hrCount++;
            }
            if (r.getTotalCholesterol() > 0) {
                totalTc += r.getTotalCholesterol();
                tcCount++;
            }
            if (r.getLdl() > 0) {
                totalLdl += r.getLdl();
                ldlCount++;
            }
            if (r.getHdl() > 0) {
                totalHdl += r.getHdl();
                hdlCount++;
            }

            String category = r.getAHACategory();
            if ("Normal".equals(category)) normal++;
            else if ("Elevated".equals(category)) elevated++;
            else if ("Stage 1".equals(category)) stage1++;
            else if ("Stage 2".equals(category)) stage2++;
            else if ("Crisis".equals(category)) crisis++;
        }

        double avgSys = totalSys / (double) records.size();
        double avgDia = totalDia / (double) records.size();

        StringBuilder sb = new StringBuilder();
        sb.append("Record count: ").append(records.size()).append("\n\n");
        sb.append(String.format("Average BP: %.1f / %.1f mmHg%n", avgSys, avgDia));
        sb.append("Average Heart Rate: ").append(avgOrDash(totalHr, hrCount)).append("\n");
        sb.append("Average Total Cholesterol: ").append(avgOrDash(totalTc, tcCount)).append(" mg/dL\n");
        sb.append("Average LDL: ").append(avgOrDash(totalLdl, ldlCount)).append(" mg/dL\n");
        sb.append("Average HDL: ").append(avgOrDash(totalHdl, hdlCount)).append(" mg/dL\n\n");

        sb.append("Blood Pressure Risk Level Distribution (AHA guideline):\n");
        sb.append("  Normal: ").append(normal).append("\n");
        sb.append("  Elevated: ").append(elevated).append("\n");
        sb.append("  Stage 1: ").append(stage1).append("\n");
        sb.append("  Stage 2: ").append(stage2).append("\n");
        sb.append("  Crisis: ").append(crisis).append("\n\n");

        if (crisis > 0) {
            sb.append("Alert: Crisis readings detected. Seek medical guidance immediately.\n");
        } else if (stage2 >= 3) {
            sb.append("Alert: Multiple Stage 2 readings detected.\n");
        } else {
            sb.append("No high-risk pattern detected in this window.\n");
        }

        analyticsArea.setText(sb.toString());

        List<BPRecord> chronological = new ArrayList<>(records);
        java.util.Collections.reverse(chronological);
        int maxPoints = 14;
        if (chronological.size() > maxPoints) {
            chronological = chronological.subList(chronological.size() - maxPoints, chronological.size());
        }

        List<String> labels = new ArrayList<>();
        List<Integer> sysValues = new ArrayList<>();
        List<Integer> diaValues = new ArrayList<>();
        List<Integer> ldlValues = new ArrayList<>();
        List<Integer> hdlValues = new ArrayList<>();
        SimpleDateFormat labelFmt = new SimpleDateFormat("MM-dd");
        for (BPRecord r : chronological) {
            labels.add(labelFmt.format(new Date(r.getTimestamp())));
            sysValues.add(r.getSystolic());
            diaValues.add(r.getDiastolic());
            ldlValues.add(r.getLdl());
            hdlValues.add(r.getHdl());
        }
        bpChartPanel.setSeries(labels, sysValues, diaValues);
        ldlChartPanel.setSeries(labels, ldlValues, new ArrayList<>());
        hdlChartPanel.setSeries(labels, hdlValues, new ArrayList<>());
    }

    private JPanel buildExportPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_DARK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(48, 48, 48, 48));

        JLabel heading = new JLabel("Export to CSV");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(TEXT_LIGHT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(16));

        JLabel desc = new JLabel("Export all readings to bloodhound_export.csv in the current folder.");
        desc.setForeground(TEXT_MUTED);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(desc);
        panel.add(Box.createVerticalStrut(28));

        JButton exportBtn = new JButton("Export Now");
        stylePrimaryButton(exportBtn);
        exportBtn.addActionListener(e -> {
            List<BPRecord> records = db.getAllRecords();
            File outDir = new File(".").getAbsoluteFile();
            File file = ExportServiceDesktop.exportToCSV(outDir, records);
            if (file != null) {
                statusLabel.setText("Exported to " + file.getAbsolutePath());
                JOptionPane.showMessageDialog(frame,
                        "Exported " + records.size() + " records to:\n" + file.getAbsolutePath(),
                        "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Export failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        exportBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(exportBtn);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 24, 12, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT);
            }
        });
    }

    private JLabel newLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lbl;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(BG_CARD);
        combo.setForeground(TEXT_LIGHT);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void styleTextField(JTextField field) {
        field.setBackground(BG_CARD);
        field.setForeground(TEXT_LIGHT);
        field.setCaretColor(TEXT_LIGHT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                new EmptyBorder(4, 8, 4, 8)));
    }

    private int parseOptionalPositiveInt(String text) {
        if (text == null || text.isEmpty()) return 0;
        int value = Integer.parseInt(text);
        if (value < 0) throw new NumberFormatException("Negative number");
        return value;
    }

    private List<BPRecord> filterByCategory(List<BPRecord> records, String category) {
        if (category == null || "All".equals(category)) return records;
        List<BPRecord> filtered = new ArrayList<>();
        for (BPRecord r : records) {
            if (category.equals(r.getAHACategory())) filtered.add(r);
        }
        return filtered;
    }

    private List<BPRecord> getFilteredReadings() {
        List<BPRecord> records = filterByCategory(db.getAllRecords(), (String) categoryFilter.getSelectedItem());
        Long from = parseDateBoundary(fromDateField.getText().trim(), true);
        Long to = parseDateBoundary(toDateField.getText().trim(), false);
        if (from == null && to == null) return records;
        List<BPRecord> filtered = new ArrayList<>();
        for (BPRecord r : records) {
            boolean ok = true;
            if (from != null && r.getTimestamp() < from) ok = false;
            if (to != null && r.getTimestamp() > to) ok = false;
            if (ok) filtered.add(r);
        }
        return filtered;
    }

    private Long parseDateBoundary(String value, boolean startOfDay) {
        if (value == null || value.isEmpty()) return null;
        try {
            LocalDate date = LocalDate.parse(value);
            if (startOfDay) {
                return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            }
            return date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(frame, "Date must be yyyy-mm-dd", "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private List<BPRecord> filterByWindow(List<BPRecord> records, String window) {
        if (window == null || "All".equals(window)) return records;
        long now = System.currentTimeMillis();
        long days;
        if ("7 Days".equals(window)) days = 7L;
        else if ("30 Days".equals(window)) days = 30L;
        else days = 90L;
        long threshold = now - (days * 24L * 60L * 60L * 1000L);
        List<BPRecord> filtered = new ArrayList<>();
        for (BPRecord r : records) {
            if (r.getTimestamp() >= threshold) filtered.add(r);
        }
        return filtered;
    }

    private String nonZeroOrDash(int value) {
        return value > 0 ? String.valueOf(value) : "-";
    }

    private String avgOrDash(int total, int count) {
        if (count == 0) return "-";
        return String.format("%.1f", total / (double) count);
    }

    private String shortId(String id) {
        if (id == null) return "-";
        return id.length() <= 8 ? id : id.substring(0, 8);
    }

    private void deleteBySessionId() {
        String idInput = sessionIdField.getText().trim();
        if (idInput.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Enter full Session ID to delete.", "Missing Session ID", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sessionId = resolveSessionId(idInput);
        if (sessionId == null) return;
        int confirm = JOptionPane.showConfirmDialog(frame, "Delete record " + sessionId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        boolean deleted = db.deleteBySessionId(sessionId);
        if (deleted) {
            statusLabel.setText("Deleted record " + shortId(sessionId));
            refreshReadings();
            refreshAnalytics();
        } else {
            JOptionPane.showMessageDialog(frame, "Session ID not found.", "Delete Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editBySessionId() {
        String idInput = sessionIdField.getText().trim();
        if (idInput.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Enter full Session ID to edit.", "Missing Session ID", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sessionId = resolveSessionId(idInput);
        if (sessionId == null) return;
        BPRecord existing = null;
        for (BPRecord r : db.getAllRecords()) {
            if (r.getSessionId().equals(sessionId)) {
                existing = r;
                break;
            }
        }
        if (existing == null) {
            JOptionPane.showMessageDialog(frame, "Session ID not found.", "Edit Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int systolic = parseEditValue("Systolic", existing.getSystolic());
            int diastolic = parseEditValue("Diastolic", existing.getDiastolic());
            int heartRate = parseEditValue("Heart Rate", existing.getHeartRate());
            int tc = parseEditValue("Total Cholesterol", existing.getTotalCholesterol());
            int ldl = parseEditValue("LDL", existing.getLdl());
            int hdl = parseEditValue("HDL", existing.getHdl());

            BPRecord updated = new BPRecord(
                    existing.getSessionId(),
                    existing.getTimestamp(),
                    systolic,
                    diastolic,
                    heartRate,
                    existing.getTimeOfDay(),
                    existing.getMedTiming(),
                    existing.getActivityTiming(),
                    tc,
                    ldl,
                    hdl
            );
            boolean ok = db.updateRecord(updated);
            if (ok) {
                statusLabel.setText("Updated record " + shortId(sessionId));
                refreshReadings();
                refreshAnalytics();
            } else {
                JOptionPane.showMessageDialog(frame, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ignored) {
            JOptionPane.showMessageDialog(frame, "Edit cancelled or invalid value.", "Edit", JOptionPane.WARNING_MESSAGE);
        }
    }

    private int parseEditValue(String label, int current) {
        String input = JOptionPane.showInputDialog(frame, label + ":", String.valueOf(current));
        if (input == null) throw new NumberFormatException("Cancelled");
        input = input.trim();
        if (input.isEmpty()) return current;
        int value = Integer.parseInt(input);
        if (value < 0) throw new NumberFormatException("Negative");
        return value;
    }

    private String resolveSessionId(String input) {
        List<BPRecord> records = db.getAllRecords();
        for (BPRecord r : records) {
            if (r.getSessionId().equals(input)) return input;
        }
        List<String> matches = new ArrayList<>();
        for (BPRecord r : records) {
            if (r.getSessionId().startsWith(input)) matches.add(r.getSessionId());
        }
        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Session ID not found.", "Lookup Failed", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (matches.size() > 1) {
            JOptionPane.showMessageDialog(frame, "Session ID prefix matches multiple records. Enter more characters.", "Lookup Ambiguous", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return matches.get(0);
    }

    private static class NavButton {
        final String label;
        final String card;
        NavButton(String label, String card) {
            this.label = label;
            this.card = card;
        }
    }

    private class BarChartPanel extends JPanel {
        private final String title;
        private final String seriesALabel;
        private final String seriesBLabel;
        private List<String> labels = new ArrayList<>();
        private List<Integer> seriesA = new ArrayList<>();
        private List<Integer> seriesB = new ArrayList<>();

        BarChartPanel(String title, String seriesALabel, String seriesBLabel) {
            this.title = title;
            this.seriesALabel = seriesALabel;
            this.seriesBLabel = seriesBLabel;
            setBackground(BG_CARD);
            setBorder(new EmptyBorder(8, 10, 8, 10));
        }

        void setSeries(List<String> labels, List<Integer> seriesA, List<Integer> seriesB) {
            this.labels = new ArrayList<>(labels);
            this.seriesA = new ArrayList<>(seriesA);
            this.seriesB = new ArrayList<>(seriesB);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int left = 36;
            int right = 10;
            int top = 24;
            int bottom = 20;
            int chartW = Math.max(10, w - left - right);
            int chartH = Math.max(10, h - top - bottom);

            g2.setColor(TEXT_LIGHT);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.drawString(title, 6, 14);

            if (labels.isEmpty() || seriesA.isEmpty()) {
                g2.setColor(TEXT_MUTED);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.drawString("No data", left, top + chartH / 2);
                g2.dispose();
                return;
            }

            int max = 1;
            for (int v : seriesA) max = Math.max(max, v);
            for (int v : seriesB) max = Math.max(max, v);

            g2.setColor(new Color(100, 116, 139));
            g2.drawLine(left, top, left, top + chartH);
            g2.drawLine(left, top + chartH, left + chartW, top + chartH);

            int n = labels.size();
            int slotW = Math.max(6, chartW / n);
            int barW = (seriesB.isEmpty() ? Math.max(2, slotW - 4) : Math.max(2, (slotW - 4) / 2));
            for (int i = 0; i < n; i++) {
                int xBase = left + i * slotW + 2;
                int a = i < seriesA.size() ? seriesA.get(i) : 0;
                int aH = (int) ((a / (double) max) * (chartH - 4));
                g2.setColor(new Color(59, 130, 246));
                g2.fillRect(xBase, top + chartH - aH, barW, aH);

                if (!seriesB.isEmpty()) {
                    int b = i < seriesB.size() ? seriesB.get(i) : 0;
                    int bH = (int) ((b / (double) max) * (chartH - 4));
                    g2.setColor(new Color(248, 113, 113));
                    g2.fillRect(xBase + barW + 2, top + chartH - bH, barW, bH);
                }

                if (i % 2 == 0 || n <= 7) {
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                    g2.drawString(labels.get(i), xBase, top + chartH + 12);
                }
            }

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(59, 130, 246));
            g2.drawString(seriesALabel, w - 110, 14);
            if (seriesBLabel != null) {
                g2.setColor(new Color(248, 113, 113));
                g2.drawString(seriesBLabel, w - 55, 14);
            }
            g2.dispose();
        }
    }
}
