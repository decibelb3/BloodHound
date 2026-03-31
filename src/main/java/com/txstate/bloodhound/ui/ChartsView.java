package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.MetricPoint;
import com.txstate.bloodhound.util.OperationResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * JavaFX chart screen for viewing user health trends over time.
 * <p>
 * Data is loaded through {@link DashboardViewController} and ultimately sourced
 * from {@code ChartDataService}.
 */
public class ChartsView {
    private static final DateTimeFormatter DATE_TIME_TOOLTIP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final DashboardViewController controller;
    private final Runnable onBackToDashboard;

    private final BorderPane root = new BorderPane();
    private final DatePicker startDatePicker = new DatePicker();
    private final DatePicker endDatePicker = new DatePicker();
    private final TextField startTimeField = new TextField("00:00");
    private final TextField endTimeField = new TextField("23:59");
    private final Label feedbackLabel = new Label();
    private final Label bpEmptyLabel = new Label();
    private final Label cholesterolEmptyLabel = new Label();
    private final Label weightEmptyLabel = new Label();

    private final LineChart<Number, Number> bloodPressureChart;
    private final LineChart<Number, Number> cholesterolChart;
    private final LineChart<Number, Number> weightChart;

    private final Button applyFilterButton = new Button("Apply Date Filter");
    private final Button clearFilterButton = new Button("Clear Filter");
    private final Button refreshButton = new Button("Refresh");
    private final Button backButton = new Button("Back to Dashboard");

    public ChartsView(DashboardViewController controller, Runnable onBackToDashboard) {
        this.controller = Objects.requireNonNull(controller, "controller must not be null");
        this.onBackToDashboard = Objects.requireNonNull(onBackToDashboard, "onBackToDashboard must not be null");
        this.bloodPressureChart = createChart("Blood Pressure Trend", "Blood Pressure");
        this.cholesterolChart = createChart("Cholesterol Trend", "Cholesterol");
        this.weightChart = createChart("Weight Trend", "Weight");
        build();
        loadCharts(getCurrentRangeStart(), getCurrentRangeEnd());
    }

    /**
     * Returns root node for scene construction.
     *
     * @return charts screen root
     */
    public Parent getRoot() {
        return root;
    }

    private void build() {
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #f4f7fb;");

        Label title = new Label("Trend Charts");
        title.setFont(Font.font(22));
        title.setTextFill(Color.web("#1f2a44"));
        Label subtitle = new Label("Visualize blood pressure, cholesterol, and weight over time.");
        subtitle.setTextFill(Color.web("#5b6b87"));
        VBox titleBox = new VBox(4, title, subtitle);

        HBox actionButtons = new HBox(8, refreshButton, backButton);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(12, titleBox, spacer, actionButtons);
        topBar.setAlignment(Pos.CENTER_LEFT);

        HBox filterBar = new HBox(8,
                new Label("Start Date:"), startDatePicker,
                new Label("Start Time (HH:mm):"), startTimeField,
                new Label("End Date:"), endDatePicker,
                new Label("End Time (HH:mm):"), endTimeField,
                applyFilterButton, clearFilterButton);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(10));
        filterBar.setStyle("-fx-background-color: white; -fx-border-color: #d9e1ef; "
                + "-fx-background-radius: 8; -fx-border-radius: 8;");

        VBox bloodPressureSection = chartSection("Blood Pressure Trend Chart", bloodPressureChart, bpEmptyLabel);
        VBox cholesterolSection = chartSection("Cholesterol Trend Chart", cholesterolChart, cholesterolEmptyLabel);
        VBox weightSection = chartSection("Weight Trend Chart", weightChart, weightEmptyLabel);

        feedbackLabel.setWrapText(true);
        feedbackLabel.setTextFill(Color.web("#b00020"));

        VBox content = new VBox(12, filterBar, bloodPressureSection, cholesterolSection, weightSection, feedbackLabel);
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent;");

        root.setTop(topBar);
        root.setCenter(scrollPane);
        BorderPane.setMargin(topBar, new Insets(0, 0, 12, 0));

        attachActions();
    }

    private void attachActions() {
        backButton.setOnAction(event -> onBackToDashboard.run());
        refreshButton.setOnAction(event -> loadCharts(getCurrentRangeStart(), getCurrentRangeEnd()));
        applyFilterButton.setOnAction(event -> applyDateFilter());
        clearFilterButton.setOnAction(event -> {
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            UiInputUtil.resetTimeRangeInputs(startTimeField, endTimeField);
            controller.getAppState().clearDateTimeFilter();
            loadCharts(null, null);
        });
    }

    private void applyDateFilter() {
        OperationResult<UiInputUtil.DateTimeRange> rangeResult = UiInputUtil.resolveDateTimeRange(
                startDatePicker.getValue(),
                startTimeField.getText(),
                endDatePicker.getValue(),
                endTimeField.getText());
        if (!rangeResult.isSuccess() || rangeResult.getData() == null) {
            setError(UiInputUtil.formatOperationErrors(rangeResult));
            return;
        }
        UiInputUtil.DateTimeRange range = rangeResult.getData();
        controller.getAppState().setDateTimeFilter(range.start(), range.end());
        loadCharts(range.start(), range.end());
    }

    private void loadCharts(LocalDateTime startInclusive, LocalDateTime endInclusive) {
        try {
            List<MetricPoint> systolic = controller.loadTrendPoints("systolic", startInclusive, endInclusive);
            List<MetricPoint> diastolic = controller.loadTrendPoints("diastolic", startInclusive, endInclusive);
            List<MetricPoint> totalChol = controller.loadTrendPoints("totalCholesterol", startInclusive, endInclusive);
            List<MetricPoint> hdl = controller.loadTrendPoints("hdl", startInclusive, endInclusive);
            List<MetricPoint> ldl = controller.loadTrendPoints("ldl", startInclusive, endInclusive);
            List<MetricPoint> weight = controller.loadTrendPoints("weight", startInclusive, endInclusive);

            renderChart(
                    bloodPressureChart,
                    List.of(
                            series("Systolic", systolic),
                            series("Diastolic", diastolic)));
            renderChart(
                    cholesterolChart,
                    List.of(
                            series("Total Cholesterol", totalChol),
                            series("HDL", hdl),
                            series("LDL", ldl)));
            renderChart(
                    weightChart,
                    List.of(series("Weight", weight)));

            setEmptyLabel(bpEmptyLabel, hasAnyData(systolic, diastolic), "No blood pressure data for the selected range.");
            setEmptyLabel(cholesterolEmptyLabel, hasAnyData(totalChol, hdl, ldl), "No cholesterol data for the selected range.");
            setEmptyLabel(weightEmptyLabel, !weight.isEmpty(), "No weight data for the selected range.");
            clearFeedback();
        } catch (Exception exception) {
            setError("Unable to load chart data: " + exception.getMessage());
        }
    }

    private VBox chartSection(String title, LineChart<Number, Number> chart, Label emptyLabel) {
        Label sectionTitle = new Label(title);
        sectionTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #25324a;");

        emptyLabel.setTextFill(Color.web("#5b6b87"));
        emptyLabel.setVisible(false);
        emptyLabel.setManaged(false);

        VBox section = new VBox(8, sectionTitle, chart, emptyLabel);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d9e1ef; "
                + "-fx-background-radius: 10; -fx-border-radius: 10;");
        return section;
    }

    private LineChart<Number, Number> createChart(String title, String yAxisLabel) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Measurement Date/Time");
        xAxis.setForceZeroInRange(false);
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number value) {
                LocalDateTime dateTime = epochSecondsToLocalDateTime(value);
                return DATE_TIME_TOOLTIP_FORMATTER.format(dateTime);
            }
        });

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);
        yAxis.setForceZeroInRange(false);

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setAnimated(false);
        chart.setCreateSymbols(true);
        chart.setMinHeight(300);
        return chart;
    }

    private void renderChart(LineChart<Number, Number> chart, List<XYChart.Series<Number, Number>> seriesList) {
        chart.getData().setAll(seriesList);
    }

    private XYChart.Series<Number, Number> series(String name, List<MetricPoint> points) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (MetricPoint point : points) {
            if (point.getTimestamp() == null || point.getValue() == null) {
                continue;
            }
            XYChart.Data<Number, Number> data = new XYChart.Data<>(
                    localDateTimeToEpochSeconds(point.getTimestamp()),
                    point.getValue());
            series.getData().add(data);
        }
        return series;
    }

    private boolean hasAnyData(List<MetricPoint>... dataSets) {
        for (List<MetricPoint> dataSet : dataSets) {
            if (dataSet != null && !dataSet.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void setEmptyLabel(Label label, boolean hasData, String message) {
        label.setText(message);
        label.setVisible(!hasData);
        label.setManaged(!hasData);
    }

    private LocalDateTime currentStart() {
        return controller.getAppState().getFilterStartDateTime();
    }

    private LocalDateTime currentEnd() {
        return controller.getAppState().getFilterEndDateTime();
    }

    private LocalDateTime getCurrentRangeStart() {
        applyStoredDateRangeToInputs();
        return controller.getAppState().getFilterStartDateTime();
    }

    private LocalDateTime getCurrentRangeEnd() {
        applyStoredDateRangeToInputs();
        return controller.getAppState().getFilterEndDateTime();
    }

    private void applyStoredDateRangeToInputs() {
        UiInputUtil.applyDateTimeRangeToInputs(
                controller.getAppState().getFilterStartDateTime(),
                controller.getAppState().getFilterEndDateTime(),
                startDatePicker,
                startTimeField,
                endDatePicker,
                endTimeField,
                "00:00",
                "23:59");
    }

    private long localDateTimeToEpochSeconds(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    private LocalDateTime epochSecondsToLocalDateTime(Number value) {
        long epochSeconds = value.longValue();
        return LocalDateTime.ofInstant(java.time.Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault());
    }

    private void setError(String message) {
        feedbackLabel.setTextFill(Color.web("#b00020"));
        feedbackLabel.setText(message);
    }

    private void clearFeedback() {
        feedbackLabel.setText("");
    }
}
