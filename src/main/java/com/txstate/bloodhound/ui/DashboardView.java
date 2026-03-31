package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.DashboardSummary;
import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.model.MetricPoint;
import com.txstate.bloodhound.model.User;
import com.txstate.bloodhound.util.OperationResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * JavaFX main dashboard view shown after successful login.
 * <p>
 * Includes summary cards, navigation actions, date filtering controls,
 * chart placeholder panels, and refresh support.
 */
public class DashboardView {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final DashboardViewController controller;
    private final User user;
    private final Runnable onLogout;
    private final Consumer<Runnable> onOpenAddMeasurement;
    private final Consumer<Runnable> onOpenHistory;
    private final Consumer<Runnable> onOpenCharts;

    private final BorderPane root = new BorderPane();
    private final Label feedbackLabel = new Label();

    private final Label latestBpValue = new Label("N/A");
    private final Label latestTotalCholValue = new Label("N/A");
    private final Label latestHdlValue = new Label("N/A");
    private final Label latestLdlValue = new Label("N/A");
    private final Label latestWeightValue = new Label("N/A");

    private final Label averageBpValue = new Label("N/A");
    private final Label averageTotalCholValue = new Label("N/A");
    private final Label averageHdlValue = new Label("N/A");
    private final Label averageLdlValue = new Label("N/A");
    private final Label averageWeightValue = new Label("N/A");

    private final Label historySummaryLabel = new Label("History: not loaded");
    private final Label chartSummaryLabel = new Label("Charts: not loaded");

    private final DatePicker startDatePicker = new DatePicker();
    private final DatePicker endDatePicker = new DatePicker();
    private final TextField startTimeField = new TextField("00:00");
    private final TextField endTimeField = new TextField("23:59");

    private final Button refreshButton = new Button("Refresh");
    private final Button addMeasurementButton = new Button("Add Measurement");
    private final Button viewHistoryButton = new Button("View History");
    private final Button filterByDateButton = new Button("Filter by Date Range");
    private final Button viewChartsButton = new Button("View Charts");
    private final Button logoutButton = new Button("Log Out");

    public DashboardView(DashboardViewController controller,
                         User user,
                         Runnable onLogout,
                         Consumer<Runnable> onOpenAddMeasurement,
                         Consumer<Runnable> onOpenHistory,
                         Consumer<Runnable> onOpenCharts) {
        this.controller = Objects.requireNonNull(controller, "controller must not be null");
        this.user = Objects.requireNonNull(user, "user must not be null");
        this.onLogout = Objects.requireNonNull(onLogout, "onLogout must not be null");
        this.onOpenAddMeasurement = Objects.requireNonNull(onOpenAddMeasurement, "onOpenAddMeasurement must not be null");
        this.onOpenHistory = Objects.requireNonNull(onOpenHistory, "onOpenHistory must not be null");
        this.onOpenCharts = Objects.requireNonNull(onOpenCharts, "onOpenCharts must not be null");
        build();
        refresh();
    }

    /**
     * Returns root node for scene construction.
     *
     * @return dashboard root
     */
    public Parent getRoot() {
        return root;
    }

    public Button getLogoutButton() {
        return logoutButton;
    }

    /**
     * Refreshes dashboard summary cards and selected content panels.
     */
    public void refresh() {
        applyStoredDateRangeToInputs();
        refreshSummaryCards(getCurrentRangeStart(), getCurrentRangeEnd());
        loadHistorySummary();
        loadChartSummary();
    }

    private void build() {
        root.setStyle("-fx-background-color: #f4f7fb;");
        root.setPadding(new Insets(16));

        root.setTop(buildTopBar());
        root.setCenter(buildBody());
        root.setBottom(buildFeedbackBar());

        attachActions();
    }

    private HBox buildTopBar() {
        Label title = new Label("BloodHound 2.0 Dashboard");
        title.setFont(Font.font(22));
        title.setTextFill(Color.web("#1f2a44"));

        Label subtitle = new Label("Signed in as " + user.getUsername());
        subtitle.setTextFill(Color.web("#5b6b87"));

        VBox titleStack = new VBox(4, title, subtitle);

        HBox actions = new HBox(8, refreshButton, logoutButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox top = new HBox(12, titleStack, spacer, actions);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(0, 0, 12, 0));
        return top;
    }

    private ScrollPane buildBody() {
        VBox content = new VBox(14,
                buildSummaryCardsSection(),
                buildActionSection(),
                buildHistorySection(),
                buildChartsSection());
        content.setPadding(new Insets(4, 0, 10, 0));

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent;");
        return scrollPane;
    }

    private VBox buildSummaryCardsSection() {
        Label sectionTitle = sectionLabel("Summary");

        GridPane cards = new GridPane();
        cards.setHgap(10);
        cards.setVgap(10);

        cards.add(summaryCard("Latest Blood Pressure", latestBpValue), 0, 0);
        cards.add(summaryCard("Latest Total Cholesterol", latestTotalCholValue), 1, 0);
        cards.add(summaryCard("Latest HDL", latestHdlValue), 2, 0);
        cards.add(summaryCard("Latest LDL", latestLdlValue), 0, 1);
        cards.add(summaryCard("Latest Weight", latestWeightValue), 1, 1);
        cards.add(summaryCard("Average Blood Pressure", averageBpValue), 2, 1);
        cards.add(summaryCard("Average Total Cholesterol", averageTotalCholValue), 0, 2);
        cards.add(summaryCard("Average HDL", averageHdlValue), 1, 2);
        cards.add(summaryCard("Average LDL", averageLdlValue), 2, 2);
        cards.add(summaryCard("Average Weight", averageWeightValue), 0, 3);

        VBox section = cardSection(sectionTitle, cards);
        return section;
    }

    private VBox buildActionSection() {
        Label sectionTitle = sectionLabel("Navigation / Actions");

        HBox actionRow = new HBox(8,
                addMeasurementButton,
                viewHistoryButton,
                filterByDateButton,
                viewChartsButton);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        HBox dateFilterRow = new HBox(8,
                new Label("Start Date:"), startDatePicker,
                new Label("Start Time (HH:mm):"), startTimeField,
                new Label("End Date:"), endDatePicker,
                new Label("End Time (HH:mm):"), endTimeField);
        dateFilterRow.setAlignment(Pos.CENTER_LEFT);

        VBox sectionContent = new VBox(8, actionRow, dateFilterRow);
        return cardSection(sectionTitle, sectionContent);
    }

    private VBox buildHistorySection() {
        Label sectionTitle = sectionLabel("History");
        historySummaryLabel.setWrapText(true);
        VBox content = new VBox(historySummaryLabel);
        return cardSection(sectionTitle, content);
    }

    private VBox buildChartsSection() {
        Label sectionTitle = sectionLabel("Charts");
        chartSummaryLabel.setWrapText(true);
        Label placeholder = new Label("Line chart placeholders are prepared for metric trends over time.");
        placeholder.setTextFill(Color.web("#5b6b87"));
        VBox content = new VBox(6, chartSummaryLabel, placeholder);
        return cardSection(sectionTitle, content);
    }

    private HBox buildFeedbackBar() {
        feedbackLabel.setWrapText(true);
        feedbackLabel.setTextFill(Color.web("#b00020"));
        HBox bar = new HBox(feedbackLabel);
        bar.setPadding(new Insets(10, 0, 0, 0));
        return bar;
    }

    private VBox summaryCard(String title, Label valueLabel) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #5b6b87;");

        valueLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1f2a44;");

        VBox card = new VBox(5, titleLabel, valueLabel);
        card.setPadding(new Insets(12));
        card.setMinWidth(220);
        card.setStyle("-fx-background-color: white; -fx-border-color: #d9e1ef; "
                + "-fx-background-radius: 8; -fx-border-radius: 8;");
        return card;
    }

    private VBox cardSection(Label title, javafx.scene.Node content) {
        VBox box = new VBox(8, title, content);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d9e1ef; "
                + "-fx-background-radius: 10; -fx-border-radius: 10;");
        return box;
    }

    private Label sectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #25324a;");
        return label;
    }

    private void attachActions() {
        refreshButton.setOnAction(event -> refresh());
        logoutButton.setOnAction(event -> onLogout.run());

        addMeasurementButton.setOnAction(event -> onOpenAddMeasurement.accept(this::refresh));

        viewHistoryButton.setOnAction(event -> onOpenHistory.accept(this::refresh));
        filterByDateButton.setOnAction(event -> applyDateRangeFilterAcrossDashboard());
        viewChartsButton.setOnAction(event -> onOpenCharts.accept(this::refresh));
    }

    private void refreshSummaryCards(LocalDateTime startInclusive, LocalDateTime endInclusive) {
        try {
            OperationResult<DashboardSummary> result = (startInclusive != null && endInclusive != null)
                    ? controller.loadDashboardSummary(startInclusive, endInclusive)
                    : controller.loadDashboardSummary();
            if (!result.isSuccess() || result.getData() == null) {
                setFeedback(formatOperationError(result));
                return;
            }
            DashboardSummary summary = result.getData();
            latestBpValue.setText(formatBloodPressure(summary.getLatestSystolic(), summary.getLatestDiastolic()));
            latestTotalCholValue.setText(formatNumber(summary.getLatestTotalCholesterol()));
            latestHdlValue.setText(formatNumber(summary.getLatestHdl()));
            latestLdlValue.setText(formatNumber(summary.getLatestLdl()));
            latestWeightValue.setText(formatWeight(summary.getLatestWeight()));

            averageBpValue.setText(formatBloodPressure(summary.getAverageSystolic(), summary.getAverageDiastolic()));
            averageTotalCholValue.setText(formatDecimal(summary.getAverageTotalCholesterol()));
            averageHdlValue.setText(formatDecimal(summary.getAverageHdl()));
            averageLdlValue.setText(formatDecimal(summary.getAverageLdl()));
            averageWeightValue.setText(formatWeight(summary.getAverageWeight()));

            clearFeedback();
        } catch (Exception exception) {
            setFeedback("Unable to load dashboard summary: " + exception.getMessage());
        }
    }

    private void loadHistorySummary() {
        try {
            List<HealthMeasurement> history;
            LocalDateTime startInclusive = getCurrentRangeStart();
            LocalDateTime endInclusive = getCurrentRangeEnd();
            if (startInclusive != null && endInclusive != null) {
                OperationResult<List<HealthMeasurement>> result =
                        controller.loadCurrentUserMeasurements(startInclusive, endInclusive);
                if (!result.isSuccess()) {
                    setFeedback(formatOperationError(result));
                    return;
                }
                history = result.getData();
            } else {
                history = controller.loadCurrentUserMeasurements();
            }
            if (history.isEmpty()) {
                historySummaryLabel.setText("No measurements found for current user.");
            } else {
                HealthMeasurement first = history.get(0);
                historySummaryLabel.setText("Loaded " + history.size()
                        + " measurements. Earliest shown: "
                        + formatDateTime(first.getMeasurementDateTime()) + ".");
            }
            clearFeedback();
        } catch (Exception exception) {
            setFeedback("Unable to load history: " + exception.getMessage());
        }
    }

    private void applyDateRangeFilterAcrossDashboard() {
        LocalDateTime start;
        LocalDateTime end;
        try {
            start = resolveDateTime(startDatePicker.getValue(), startTimeField.getText(), "start");
            end = resolveDateTime(endDatePicker.getValue(), endTimeField.getText(), "end");
        } catch (IllegalArgumentException exception) {
            setFeedback(exception.getMessage());
            return;
        }
        if (end.isBefore(start)) {
            setFeedback("End date/time cannot be before start date/time.");
            return;
        }
        controller.getAppState().setDateTimeFilter(start, end);

        refreshSummaryCards(start, end);
        loadHistorySummary();
        loadChartSummary();
        if (feedbackLabel.getText() == null || feedbackLabel.getText().isBlank()) {
            setFeedback("Applied date range filter.");
        }
    }

    private void loadChartSummary() {
        LocalDateTime start = getCurrentRangeStart();
        LocalDateTime end = getCurrentRangeEnd();

        try {
            List<MetricPoint> systolic = controller.loadTrendPoints("systolic", start, end);
            List<MetricPoint> diastolic = controller.loadTrendPoints("diastolic", start, end);
            List<MetricPoint> totalChol = controller.loadTrendPoints("totalCholesterol", start, end);
            List<MetricPoint> hdl = controller.loadTrendPoints("hdl", start, end);
            List<MetricPoint> ldl = controller.loadTrendPoints("ldl", start, end);
            List<MetricPoint> weight = controller.loadTrendPoints("weight", start, end);

            chartSummaryLabel.setText("Trend points loaded - "
                    + "Systolic: " + systolic.size()
                    + ", Diastolic: " + diastolic.size()
                    + ", Total Cholesterol: " + totalChol.size()
                    + ", HDL: " + hdl.size()
                    + ", LDL: " + ldl.size()
                    + ", Weight: " + weight.size());
            clearFeedback();
        } catch (Exception exception) {
            setFeedback("Unable to load chart data: " + exception.getMessage());
        }
    }

    private String formatOperationError(OperationResult<?> result) {
        if (result.getErrors().isEmpty()) {
            return result.getMessage();
        }
        return result.getMessage() + " - " + String.join("; ", result.getErrors());
    }

    private String formatBloodPressure(Number systolic, Number diastolic) {
        if (systolic == null && diastolic == null) {
            return "N/A";
        }
        return formatNumber(systolic) + " / " + formatNumber(diastolic);
    }

    private String formatNumber(Number value) {
        return value == null ? "N/A" : String.valueOf(value.intValue());
    }

    private String formatDecimal(Double value) {
        return value == null ? "N/A" : String.format("%.1f", value);
    }

    private String formatWeight(Double value) {
        return value == null ? "N/A" : String.format("%.1f", value);
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "N/A" : DATE_TIME_FORMATTER.format(value);
    }

    private void applyStoredDateRangeToInputs() {
        LocalDateTime start = controller.getAppState().getFilterStartDateTime();
        LocalDateTime end = controller.getAppState().getFilterEndDateTime();
        if (start != null && end != null) {
            startDatePicker.setValue(start.toLocalDate());
            endDatePicker.setValue(end.toLocalDate());
            startTimeField.setText(start.toLocalTime().format(TIME_ONLY_FORMATTER));
            endTimeField.setText(end.toLocalTime().format(TIME_ONLY_FORMATTER));
            return;
        }
        startTimeField.setText("00:00");
        endTimeField.setText("23:59");
    }

    private LocalDateTime getCurrentRangeStart() {
        return controller.getAppState().getFilterStartDateTime();
    }

    private LocalDateTime getCurrentRangeEnd() {
        return controller.getAppState().getFilterEndDateTime();
    }

    private LocalDateTime resolveDateTime(LocalDate date, String timeText, String label) {
        if (date == null) {
            throw new IllegalArgumentException("Select a " + label + " date.");
        }
        String rawTime = timeText == null ? "" : timeText.trim();
        if (rawTime.isBlank()) {
            throw new IllegalArgumentException("Select a " + label + " time (HH:mm).");
        }
        try {
            LocalTime time = LocalTime.parse(rawTime, TIME_ONLY_FORMATTER);
            return LocalDateTime.of(date, time);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid " + label + " time. Use HH:mm.");
        }
    }

    private void setFeedback(String text) {
        feedbackLabel.setText(text);
    }

    private void clearFeedback() {
        feedbackLabel.setText("");
    }
}
