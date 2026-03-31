package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.util.OperationResult;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * JavaFX history screen for viewing and managing user measurements.
 */
public class HistoryView {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final DashboardViewController controller;
    private final Runnable onBackToDashboard;
    private final Runnable onHistoryChanged;

    private final BorderPane root = new BorderPane();
    private final TableView<HealthMeasurement> measurementTable = new TableView<>();
    private final DatePicker startDatePicker = new DatePicker();
    private final DatePicker endDatePicker = new DatePicker();
    private final Label feedbackLabel = new Label();

    private final TextField editSystolicField = new TextField();
    private final TextField editDiastolicField = new TextField();
    private final TextField editTotalCholesterolField = new TextField();
    private final TextField editHdlField = new TextField();
    private final TextField editLdlField = new TextField();
    private final TextField editWeightField = new TextField();
    private final DatePicker editDatePicker = new DatePicker();
    private final TextField editTimeField = new TextField();

    private final Button refreshButton = new Button("Refresh");
    private final Button applyFilterButton = new Button("Apply Date Filter");
    private final Button clearFilterButton = new Button("Clear Filter");
    private final Button saveEditButton = new Button("Save Edit");
    private final Button deleteButton = new Button("Delete Selected");
    private final Button backButton = new Button("Back to Dashboard");

    public HistoryView(DashboardViewController controller, Runnable onBackToDashboard, Runnable onHistoryChanged) {
        this.controller = Objects.requireNonNull(controller, "controller must not be null");
        this.onBackToDashboard = Objects.requireNonNull(onBackToDashboard, "onBackToDashboard must not be null");
        this.onHistoryChanged = Objects.requireNonNull(onHistoryChanged, "onHistoryChanged must not be null");
        build();
        loadHistory();
    }

    /**
     * Returns root node for scene construction.
     *
     * @return history screen root
     */
    public Parent getRoot() {
        return root;
    }

    private void build() {
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #f4f7fb;");

        Label title = new Label("Measurement History");
        title.setFont(Font.font(22));
        title.setTextFill(Color.web("#1f2a44"));
        Label subtitle = new Label("View, filter, edit, and delete your measurements.");
        subtitle.setTextFill(Color.web("#5b6b87"));
        VBox titleBox = new VBox(4, title, subtitle);

        HBox topActions = new HBox(8, refreshButton, backButton);
        topActions.setAlignment(Pos.CENTER_RIGHT);
        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        HBox topBar = new HBox(12, titleBox, topSpacer, topActions);
        topBar.setAlignment(Pos.CENTER_LEFT);

        configureTable();
        measurementTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox filterBar = buildFilterBar();
        VBox editPanel = buildEditPanel();

        feedbackLabel.setWrapText(true);
        feedbackLabel.setTextFill(Color.web("#b00020"));

        VBox center = new VBox(10, filterBar, measurementTable, editPanel, feedbackLabel);
        VBox.setVgrow(measurementTable, Priority.ALWAYS);

        root.setTop(topBar);
        root.setCenter(center);
        BorderPane.setMargin(topBar, new Insets(0, 0, 12, 0));

        attachActions();
    }

    private void configureTable() {
        TableColumn<HealthMeasurement, String> measuredAtCol = new TableColumn<>("Measurement Date/Time");
        measuredAtCol.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(formatDateTime(cell.getValue().getMeasurementDateTime())));

        TableColumn<HealthMeasurement, String> systolicCol = textColumn("Systolic", value -> formatInteger(value.getSystolic()));
        TableColumn<HealthMeasurement, String> diastolicCol = textColumn("Diastolic", value -> formatInteger(value.getDiastolic()));
        TableColumn<HealthMeasurement, String> totalCholCol = textColumn("Total Cholesterol",
                value -> formatInteger(value.getTotalCholesterol()));
        TableColumn<HealthMeasurement, String> hdlCol = textColumn("HDL", value -> formatInteger(value.getHdl()));
        TableColumn<HealthMeasurement, String> ldlCol = textColumn("LDL", value -> formatInteger(value.getLdl()));
        TableColumn<HealthMeasurement, String> weightCol = textColumn("Weight", value -> formatWeight(value.getWeight()));

        measurementTable.getColumns().addAll(
                measuredAtCol, systolicCol, diastolicCol, totalCholCol, hdlCol, ldlCol, weightCol);
    }

    private TableColumn<HealthMeasurement, String> textColumn(
            String title,
            java.util.function.Function<HealthMeasurement, String> valueMapper) {
        TableColumn<HealthMeasurement, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(valueMapper.apply(cell.getValue())));
        return column;
    }

    private HBox buildFilterBar() {
        Label startLabel = new Label("Start Date:");
        Label endLabel = new Label("End Date:");
        HBox filters = new HBox(8,
                startLabel, startDatePicker,
                endLabel, endDatePicker,
                applyFilterButton, clearFilterButton);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setPadding(new Insets(10));
        filters.setStyle("-fx-background-color: white; -fx-border-color: #d9e1ef; "
                + "-fx-background-radius: 8; -fx-border-radius: 8;");
        return filters;
    }

    private VBox buildEditPanel() {
        Label panelTitle = new Label("Edit Selected Measurement");
        panelTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #25324a;");

        HBox row1 = new HBox(8,
                labeledField("Systolic", editSystolicField),
                labeledField("Diastolic", editDiastolicField),
                labeledField("Total Cholesterol", editTotalCholesterolField));
        HBox row2 = new HBox(8,
                labeledField("HDL", editHdlField),
                labeledField("LDL", editLdlField),
                labeledField("Weight", editWeightField));
        HBox row3 = new HBox(8,
                labeledField("Date", editDatePicker),
                labeledField("Time (HH:mm)", editTimeField),
                saveEditButton,
                deleteButton);
        row1.setAlignment(Pos.CENTER_LEFT);
        row2.setAlignment(Pos.CENTER_LEFT);
        row3.setAlignment(Pos.CENTER_LEFT);

        saveEditButton.setStyle("-fx-background-color: #2f6fed; -fx-text-fill: white; -fx-font-weight: bold;");

        VBox panel = new VBox(8, panelTitle, row1, row2, row3);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: white; -fx-border-color: #d9e1ef; "
                + "-fx-background-radius: 8; -fx-border-radius: 8;");
        return panel;
    }

    private VBox labeledField(String label, javafx.scene.Node field) {
        Label fieldLabel = new Label(label);
        fieldLabel.setTextFill(Color.web("#33415c"));
        VBox box = new VBox(4, fieldLabel, field);
        box.setMinWidth(150);
        return box;
    }

    private void attachActions() {
        refreshButton.setOnAction(event -> loadHistory());
        backButton.setOnAction(event -> onBackToDashboard.run());

        applyFilterButton.setOnAction(event -> applyDateFilter());
        clearFilterButton.setOnAction(event -> {
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            loadHistory();
        });

        measurementTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) ->
                populateEditor(selected));

        saveEditButton.setOnAction(event -> saveSelectedEdit());
        deleteButton.setOnAction(event -> deleteSelected());
    }

    private void loadHistory() {
        List<HealthMeasurement> measurements = sortByMeasuredAtDesc(controller.loadCurrentUserMeasurements());
        measurementTable.setItems(FXCollections.observableArrayList(measurements));
        feedbackLabel.setText("");
        feedbackLabel.setTextFill(Color.web("#b00020"));
    }

    private void applyDateFilter() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        if (startDate == null || endDate == null) {
            setError("Select both start and end dates to filter history.");
            return;
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        OperationResult<List<HealthMeasurement>> result = controller.loadCurrentUserMeasurements(start, end);
        if (!result.isSuccess()) {
            setError(formatOperationErrors(result));
            return;
        }

        measurementTable.setItems(FXCollections.observableArrayList(sortByMeasuredAtDesc(result.getData())));
        feedbackLabel.setText("");
    }

    private void populateEditor(HealthMeasurement measurement) {
        if (measurement == null) {
            clearEditor();
            return;
        }
        editSystolicField.setText(toText(measurement.getSystolic()));
        editDiastolicField.setText(toText(measurement.getDiastolic()));
        editTotalCholesterolField.setText(toText(measurement.getTotalCholesterol()));
        editHdlField.setText(toText(measurement.getHdl()));
        editLdlField.setText(toText(measurement.getLdl()));
        editWeightField.setText(measurement.getWeight() == null ? "" : String.format("%.1f", measurement.getWeight()));
        editDatePicker.setValue(measurement.getMeasurementDateTime() == null
                ? null : measurement.getMeasurementDateTime().toLocalDate());
        editTimeField.setText(measurement.getMeasurementDateTime() == null
                ? ""
                : measurement.getMeasurementDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private void saveSelectedEdit() {
        feedbackLabel.setTextFill(Color.web("#b00020"));
        HealthMeasurement selected = measurementTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setError("Select a measurement to edit.");
            return;
        }

        HealthMeasurement edited;
        try {
            edited = buildEditedMeasurement(selected);
        } catch (IllegalArgumentException exception) {
            setError(exception.getMessage());
            return;
        }

        OperationResult<HealthMeasurement> result = controller.updateMeasurementResult(edited);
        if (!result.isSuccess()) {
            setError(formatOperationErrors(result));
            return;
        }

        setSuccess(result.getMessage());
        onHistoryChanged.run();
        reloadByCurrentFilter();
    }

    private void deleteSelected() {
        feedbackLabel.setTextFill(Color.web("#b00020"));
        HealthMeasurement selected = measurementTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getMeasurementId() == null) {
            setError("Select a measurement to delete.");
            return;
        }

        OperationResult<Void> result = controller.deleteMeasurementResult(selected.getMeasurementId());
        if (!result.isSuccess()) {
            setError(formatOperationErrors(result));
            return;
        }

        setSuccess(result.getMessage());
        onHistoryChanged.run();
        reloadByCurrentFilter();
    }

    private HealthMeasurement buildEditedMeasurement(HealthMeasurement original) {
        LocalDate date = editDatePicker.getValue();
        if (date == null) {
            throw new IllegalArgumentException("Measurement date is required.");
        }
        String timeRaw = editTimeField.getText() == null ? "" : editTimeField.getText().trim();
        if (timeRaw.isBlank()) {
            throw new IllegalArgumentException("Measurement time is required (HH:mm).");
        }

        LocalTime time;
        try {
            time = LocalTime.parse(timeRaw);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Measurement time must use HH:mm format.");
        }

        HealthMeasurement edited = new HealthMeasurement();
        edited.setMeasurementId(original.getMeasurementId());
        edited.setUserId(original.getUserId());
        edited.setCreatedAt(original.getCreatedAt());
        edited.setMeasurementDateTime(LocalDateTime.of(date, time));
        edited.setSystolic(parseOptionalInteger(editSystolicField.getText(), "Systolic"));
        edited.setDiastolic(parseOptionalInteger(editDiastolicField.getText(), "Diastolic"));
        edited.setTotalCholesterol(parseOptionalInteger(editTotalCholesterolField.getText(), "Total cholesterol"));
        edited.setHdl(parseOptionalInteger(editHdlField.getText(), "HDL"));
        edited.setLdl(parseOptionalInteger(editLdlField.getText(), "LDL"));
        edited.setWeight(parseOptionalDouble(editWeightField.getText(), "Weight"));
        return edited;
    }

    private void reloadByCurrentFilter() {
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
            applyDateFilter();
            return;
        }
        loadHistory();
    }

    private void clearEditor() {
        editSystolicField.clear();
        editDiastolicField.clear();
        editTotalCholesterolField.clear();
        editHdlField.clear();
        editLdlField.clear();
        editWeightField.clear();
        editDatePicker.setValue(null);
        editTimeField.clear();
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "N/A" : DATE_TIME_FORMATTER.format(value);
    }

    private String formatInteger(Integer value) {
        return value == null ? "N/A" : String.valueOf(value);
    }

    private String formatWeight(Double value) {
        return value == null ? "N/A" : String.format("%.1f", value);
    }

    private String toText(Number value) {
        return value == null ? "" : String.valueOf(value);
    }

    private List<HealthMeasurement> sortByMeasuredAtDesc(List<HealthMeasurement> input) {
        return input.stream()
                .sorted(Comparator.comparing(
                        HealthMeasurement::getMeasurementDateTime,
                        Comparator.nullsFirst(Comparator.naturalOrder()))
                        .reversed())
                .toList();
    }

    private Integer parseOptionalInteger(String raw, String fieldName) {
        String value = raw == null ? "" : raw.trim();
        if (value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a whole number.");
        }
    }

    private Double parseOptionalDouble(String raw, String fieldName) {
        String value = raw == null ? "" : raw.trim();
        if (value.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be numeric.");
        }
    }

    private String formatOperationErrors(OperationResult<?> result) {
        List<String> messages = new ArrayList<>();
        if (result.getMessage() != null && !result.getMessage().isBlank()) {
            messages.add(result.getMessage());
        }
        if (result.getErrors() != null && !result.getErrors().isEmpty()) {
            messages.addAll(result.getErrors());
        }
        if (messages.isEmpty()) {
            return "Request failed.";
        }
        return String.join(" | ", messages);
    }

    private void setError(String message) {
        feedbackLabel.setTextFill(Color.web("#b00020"));
        feedbackLabel.setText(message);
    }

    private void setSuccess(String message) {
        feedbackLabel.setTextFill(Color.web("#1b5e20"));
        feedbackLabel.setText(message == null || message.isBlank() ? "Operation succeeded." : message);
    }
}
