package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.util.OperationResult;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
    private final TextField startTimeField = new TextField("00:00");
    private final TextField endTimeField = new TextField("23:59");
    private final Label feedbackLabel = new Label();

    private final Button refreshButton = new Button("Refresh");
    private final Button applyFilterButton = new Button("Apply Date Filter");
    private final Button clearFilterButton = new Button("Clear Filter");
    private final Button editButton = new Button("Edit Selected");
    private final Button deleteButton = new Button("Delete Selected");
    private final Button backButton = new Button("Back to Dashboard");

    public HistoryView(DashboardViewController controller, Runnable onBackToDashboard, Runnable onHistoryChanged) {
        this.controller = Objects.requireNonNull(controller, "controller must not be null");
        this.onBackToDashboard = Objects.requireNonNull(onBackToDashboard, "onBackToDashboard must not be null");
        this.onHistoryChanged = Objects.requireNonNull(onHistoryChanged, "onHistoryChanged must not be null");
        build();
        applyStoredDateRangeToInputs();
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
        measurementTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        HBox filterBar = buildFilterBar();
        HBox actionsBar = buildActionsBar();

        feedbackLabel.setWrapText(true);
        feedbackLabel.setTextFill(Color.web("#b00020"));

        VBox center = new VBox(10, filterBar, actionsBar, measurementTable, feedbackLabel);
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
        Label startTimeLabel = new Label("Start Time:");
        Label endLabel = new Label("End Date:");
        Label endTimeLabel = new Label("End Time:");
        HBox filters = new HBox(8,
                startLabel, startDatePicker,
                startTimeLabel, startTimeField,
                endLabel, endDatePicker,
                endTimeLabel, endTimeField,
                applyFilterButton, clearFilterButton);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setPadding(new Insets(10));
        filters.setStyle("-fx-background-color: white; -fx-border-color: #d9e1ef; "
                + "-fx-background-radius: 8; -fx-border-radius: 8;");
        return filters;
    }

    private HBox buildActionsBar() {
        editButton.setStyle("-fx-background-color: #2f6fed; -fx-text-fill: white; -fx-font-weight: bold;");
        HBox actionBar = new HBox(8, editButton, deleteButton);
        actionBar.setAlignment(Pos.CENTER_LEFT);
        actionBar.setPadding(new Insets(10));
        actionBar.setStyle("-fx-background-color: white; -fx-border-color: #d9e1ef; "
                + "-fx-background-radius: 8; -fx-border-radius: 8;");
        return actionBar;
    }

    private void attachActions() {
        refreshButton.setOnAction(event -> loadHistory());
        backButton.setOnAction(event -> onBackToDashboard.run());

        applyFilterButton.setOnAction(event -> applyDateFilter());
        clearFilterButton.setOnAction(event -> {
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            UiInputUtil.resetTimeFields(startTimeField, endTimeField);
            controller.getAppState().clearDateTimeFilter();
            loadHistory();
        });
        editButton.setOnAction(event -> editSelected());
        deleteButton.setOnAction(event -> deleteSelected());
    }

    private void loadHistory() {
        if (controller.getAppState().hasDateTimeFilter()) {
            LocalDateTime start = controller.getAppState().getFilterStartDateTime();
            LocalDateTime end = controller.getAppState().getFilterEndDateTime();
            if (start != null && end != null) {
                loadHistoryByRange(start, end);
                return;
            }
        }
        showHistory(controller.loadCurrentUserMeasurements());
        clearFeedback();
    }

    private void applyDateFilter() {
        OperationResult<UiInputUtil.DateTimeRange> rangeResult = UiInputUtil.parseDateTimeRange(
                startDatePicker.getValue(),
                startTimeField.getText(),
                endDatePicker.getValue(),
                endTimeField.getText());
        if (!rangeResult.isSuccess() || rangeResult.getData() == null) {
            setError(formatOperationErrors(rangeResult));
            return;
        }
        UiInputUtil.DateTimeRange range = rangeResult.getData();
        controller.getAppState().setDateTimeFilter(range.start(), range.end());
        loadHistoryByRange(range.start(), range.end());
    }

    private void editSelected() {
        feedbackLabel.setTextFill(Color.web("#b00020"));
        HealthMeasurement selected = measurementTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setError("Select a measurement to edit.");
            return;
        }

        Optional<HealthMeasurement> editResult = showEditDialog(selected);
        if (editResult.isEmpty()) {
            return;
        }

        OperationResult<HealthMeasurement> result = controller.updateMeasurementResult(editResult.get());
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

        if (!confirmDeletion(selected)) {
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

    private Optional<HealthMeasurement> showEditDialog(HealthMeasurement original) {
        Dialog<HealthMeasurement> dialog = new Dialog<>();
        dialog.setTitle("Edit Measurement");
        dialog.setHeaderText("Update the selected measurement values.");

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField systolicField = new TextField(toText(original.getSystolic()));
        TextField diastolicField = new TextField(toText(original.getDiastolic()));
        TextField totalCholesterolField = new TextField(toText(original.getTotalCholesterol()));
        TextField hdlField = new TextField(toText(original.getHdl()));
        TextField ldlField = new TextField(toText(original.getLdl()));
        TextField weightField = new TextField(original.getWeight() == null ? "" : String.valueOf(original.getWeight()));
        DatePicker datePicker = new DatePicker(original.getMeasurementDateTime() == null
                ? java.time.LocalDate.now()
                : original.getMeasurementDateTime().toLocalDate());
        TextField timeField = new TextField(original.getMeasurementDateTime() == null
                ? "08:00"
                : original.getMeasurementDateTime().toLocalTime().format(UiInputUtil.TIME_FORMATTER));
        Label dialogFeedbackLabel = new Label();
        dialogFeedbackLabel.setTextFill(Color.web("#b00020"));
        dialogFeedbackLabel.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10, 0, 0, 0));
        addDialogField(grid, 0, "Systolic:", systolicField);
        addDialogField(grid, 1, "Diastolic:", diastolicField);
        addDialogField(grid, 2, "Total Cholesterol:", totalCholesterolField);
        addDialogField(grid, 3, "HDL:", hdlField);
        addDialogField(grid, 4, "LDL:", ldlField);
        addDialogField(grid, 5, "Weight:", weightField);
        addDialogField(grid, 6, "Date:", datePicker);
        addDialogField(grid, 7, "Time (HH:mm):", timeField);
        grid.add(dialogFeedbackLabel, 0, 8, 2, 1);

        dialog.getDialogPane().setContent(grid);

        AtomicReference<HealthMeasurement> editedReference = new AtomicReference<>();
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                editedReference.set(buildEditedMeasurement(
                        original,
                        systolicField.getText(),
                        diastolicField.getText(),
                        totalCholesterolField.getText(),
                        hdlField.getText(),
                        ldlField.getText(),
                        weightField.getText(),
                        datePicker.getValue(),
                        timeField.getText()));
            } catch (IllegalArgumentException exception) {
                dialogFeedbackLabel.setText(exception.getMessage());
                event.consume();
            }
        });

        dialog.setResultConverter(buttonType ->
                buttonType == saveButtonType ? editedReference.get() : null);
        return dialog.showAndWait();
    }

    private void addDialogField(GridPane grid, int row, String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        label.setTextFill(Color.web("#33415c"));
        grid.add(label, 0, row);
        grid.add(field, 1, row);
    }

    private boolean confirmDeletion(HealthMeasurement measurement) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Measurement");
        confirmation.setHeaderText("Delete selected measurement?");
        confirmation.setContentText("Measurement date/time: " + formatDateTime(measurement.getMeasurementDateTime()));
        Optional<ButtonType> result = confirmation.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private HealthMeasurement buildEditedMeasurement(HealthMeasurement original,
                                                     String systolicText,
                                                     String diastolicText,
                                                     String totalCholesterolText,
                                                     String hdlText,
                                                     String ldlText,
                                                     String weightText,
                                                     java.time.LocalDate date,
                                                     String timeText) {
        if (date == null) {
            throw new IllegalArgumentException("Measurement date is required.");
        }
        String rawTime = timeText == null ? "" : timeText.trim();
        if (rawTime.isBlank()) {
            throw new IllegalArgumentException("Measurement time is required (HH:mm).");
        }
        LocalDateTime measuredAt;
        try {
            measuredAt = LocalDateTime.of(date, LocalTime.parse(rawTime, UiInputUtil.TIME_FORMATTER));
        } catch (Exception exception) {
            throw new IllegalArgumentException("Measurement time must use HH:mm format.");
        }

        HealthMeasurement edited = new HealthMeasurement();
        edited.setMeasurementId(original.getMeasurementId());
        edited.setUserId(original.getUserId());
        edited.setCreatedAt(original.getCreatedAt());
        edited.setMeasurementDateTime(measuredAt);
        edited.setSystolic(parseOptionalInteger(systolicText, "Systolic"));
        edited.setDiastolic(parseOptionalInteger(diastolicText, "Diastolic"));
        edited.setTotalCholesterol(parseOptionalInteger(totalCholesterolText, "Total cholesterol"));
        edited.setHdl(parseOptionalInteger(hdlText, "HDL"));
        edited.setLdl(parseOptionalInteger(ldlText, "LDL"));
        edited.setWeight(parseOptionalDouble(weightText, "Weight"));
        return edited;
    }

    private void reloadByCurrentFilter() {
        if (controller.getAppState().hasDateTimeFilter()) {
            LocalDateTime start = controller.getAppState().getFilterStartDateTime();
            LocalDateTime end = controller.getAppState().getFilterEndDateTime();
            if (start != null && end != null) {
                loadHistoryByRange(start, end);
                return;
            }
        }
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
            OperationResult<UiInputUtil.DateTimeRange> rangeResult = UiInputUtil.parseDateTimeRange(
                    startDatePicker.getValue(),
                    startTimeField.getText(),
                    endDatePicker.getValue(),
                    endTimeField.getText());
            if (rangeResult.isSuccess() && rangeResult.getData() != null) {
                UiInputUtil.DateTimeRange range = rangeResult.getData();
                controller.getAppState().setDateTimeFilter(range.start(), range.end());
                loadHistoryByRange(range.start(), range.end());
                return;
            }
            if (!rangeResult.isSuccess()) {
                setError(formatOperationErrors(rangeResult));
            }
            return;
        }
        loadHistory();
    }

    private void loadHistoryByRange(LocalDateTime start, LocalDateTime end) {
        OperationResult<List<HealthMeasurement>> result = controller.loadCurrentUserMeasurements(start, end);
        if (!result.isSuccess()) {
            setError(formatOperationErrors(result));
            return;
        }
        showHistory(result.getData());
        clearFeedback();
    }

    private void showHistory(List<HealthMeasurement> measurements) {
        measurementTable.setItems(FXCollections.observableArrayList(sortByMeasuredAtDesc(measurements)));
    }

    private void applyStoredDateRangeToInputs() {
        LocalDateTime start = controller.getAppState().getFilterStartDateTime();
        LocalDateTime end = controller.getAppState().getFilterEndDateTime();
        if (start != null && end != null) {
            startDatePicker.setValue(start.toLocalDate());
            endDatePicker.setValue(end.toLocalDate());
            startTimeField.setText(start.toLocalTime().format(UiInputUtil.TIME_FORMATTER));
            endTimeField.setText(end.toLocalTime().format(UiInputUtil.TIME_FORMATTER));
            return;
        }
        UiInputUtil.resetTimeFields(startTimeField, endTimeField);
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
        return UiInputUtil.parseOptionalInteger(raw, fieldName);
    }

    private Double parseOptionalDouble(String raw, String fieldName) {
        return UiInputUtil.parseOptionalDouble(raw, fieldName);
    }

    private String formatOperationErrors(OperationResult<?> result) {
        return UiInputUtil.formatOperationErrors(result, "Request failed.");
    }

    private void setError(String message) {
        feedbackLabel.setTextFill(Color.web("#b00020"));
        feedbackLabel.setText(message);
    }

    private void clearFeedback() {
        feedbackLabel.setTextFill(Color.web("#b00020"));
        feedbackLabel.setText("");
    }

    private void setSuccess(String message) {
        feedbackLabel.setTextFill(Color.web("#1b5e20"));
        feedbackLabel.setText(message == null || message.isBlank() ? "Operation succeeded." : message);
    }

}
