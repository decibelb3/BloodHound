package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.util.OperationResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * JavaFX view for adding a new health measurement.
 */
public class AddMeasurementView {
    private final DashboardViewController controller;
    private final Runnable onBackToDashboard;
    private final Runnable onSaveSuccess;

    private final BorderPane root = new BorderPane();

    private final TextField systolicField = new TextField();
    private final TextField diastolicField = new TextField();
    private final TextField totalCholesterolField = new TextField();
    private final TextField hdlField = new TextField();
    private final TextField ldlField = new TextField();
    private final TextField weightField = new TextField();
    private final DatePicker measurementDatePicker = new DatePicker(LocalDate.now());
    private final TextField measurementTimeField = new TextField("08:00");

    private final Label feedbackLabel = new Label();

    public AddMeasurementView(DashboardViewController controller,
                              Runnable onBackToDashboard,
                              Runnable onSaveSuccess) {
        this.controller = Objects.requireNonNull(controller, "controller must not be null");
        this.onBackToDashboard = Objects.requireNonNull(onBackToDashboard, "onBackToDashboard must not be null");
        this.onSaveSuccess = Objects.requireNonNull(onSaveSuccess, "onSaveSuccess must not be null");
        build();
    }

    /**
     * Returns the view root node for scene construction.
     *
     * @return add-measurement root
     */
    public Parent getRoot() {
        return root;
    }

    private void build() {
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #f4f7fb;");

        Label title = new Label("Add Measurement");
        title.setFont(Font.font(22));
        title.setTextFill(Color.web("#1f2a44"));
        Label subtitle = new Label("Enter one or more metrics and the exact date/time.");
        subtitle.setTextFill(Color.web("#5b6b87"));
        VBox header = new VBox(4, title, subtitle);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(12);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(16));
        formGrid.setStyle("-fx-background-color: white; -fx-border-color: #d9e1ef; "
                + "-fx-background-radius: 10; -fx-border-radius: 10;");

        int row = 0;
        addRow(formGrid, row++, "Systolic", systolicField);
        addRow(formGrid, row++, "Diastolic", diastolicField);
        addRow(formGrid, row++, "Total Cholesterol", totalCholesterolField);
        addRow(formGrid, row++, "HDL", hdlField);
        addRow(formGrid, row++, "LDL", ldlField);
        addRow(formGrid, row++, "Weight", weightField);
        addRow(formGrid, row++, "Measurement Date", measurementDatePicker);
        addRow(formGrid, row, "Measurement Time (HH:mm)", measurementTimeField);

        feedbackLabel.setWrapText(true);
        feedbackLabel.setTextFill(Color.web("#b00020"));

        Button backButton = new Button("Back to Dashboard");
        Button clearButton = new Button("Clear");
        Button saveButton = new Button("Save Measurement");
        saveButton.setStyle("-fx-background-color: #2f6fed; -fx-text-fill: white; -fx-font-weight: bold;");

        backButton.setOnAction(event -> onBackToDashboard.run());
        clearButton.setOnAction(event -> clearForm());
        saveButton.setOnAction(event -> handleSave());

        HBox actions = new HBox(8, backButton, clearButton, saveButton);
        actions.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox actionRow = new HBox(actions, spacer);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        VBox center = new VBox(12, formGrid, feedbackLabel, actionRow);
        root.setTop(header);
        root.setCenter(center);
        BorderPane.setMargin(header, new Insets(0, 0, 12, 0));
    }

    private void addRow(GridPane grid, int rowIndex, String labelText, javafx.scene.Node node) {
        Label label = new Label(labelText + ":");
        label.setTextFill(Color.web("#33415c"));
        grid.add(label, 0, rowIndex);
        grid.add(node, 1, rowIndex);
    }

    private void handleSave() {
        feedbackLabel.setTextFill(Color.web("#b00020"));

        LocalDateTime measurementDateTime;
        try {
            measurementDateTime = resolveMeasurementDateTime();
        } catch (IllegalArgumentException exception) {
            feedbackLabel.setText(exception.getMessage());
            return;
        }

        HealthMeasurement measurement;
        try {
            measurement = new HealthMeasurement();
            measurement.setSystolic(parseOptionalInteger(systolicField.getText(), "Systolic"));
            measurement.setDiastolic(parseOptionalInteger(diastolicField.getText(), "Diastolic"));
            measurement.setTotalCholesterol(parseOptionalInteger(totalCholesterolField.getText(), "Total cholesterol"));
            measurement.setHdl(parseOptionalInteger(hdlField.getText(), "HDL"));
            measurement.setLdl(parseOptionalInteger(ldlField.getText(), "LDL"));
            measurement.setWeight(parseOptionalDouble(weightField.getText(), "Weight"));
        } catch (IllegalArgumentException exception) {
            feedbackLabel.setText(exception.getMessage());
            return;
        }
        measurement.setMeasurementDateTime(measurementDateTime);

        OperationResult<HealthMeasurement> result = controller.addMeasurement(measurement);
        if (!result.isSuccess()) {
            feedbackLabel.setText(formatErrors(result));
            return;
        }

        feedbackLabel.setTextFill(Color.web("#1b5e20"));
        feedbackLabel.setText(result.getMessage());
        clearForm();
        onSaveSuccess.run();
    }

    private String formatErrors(OperationResult<?> result) {
        List<String> messages = new ArrayList<>();
        if (result.getMessage() != null && !result.getMessage().isBlank()) {
            messages.add(result.getMessage());
        }
        if (result.getErrors() != null && !result.getErrors().isEmpty()) {
            messages.addAll(result.getErrors());
        }
        return String.join("\n- ", messages.isEmpty() ? List.of("Unable to save measurement.") : withBullet(messages));
    }

    private List<String> withBullet(List<String> messages) {
        if (messages.isEmpty()) {
            return messages;
        }
        List<String> output = new ArrayList<>(messages);
        output.set(0, "- " + output.get(0));
        for (int i = 1; i < output.size(); i++) {
            output.set(i, "  - " + output.get(i));
        }
        return output;
    }

    private LocalDateTime resolveMeasurementDateTime() {
        LocalDate date = measurementDatePicker.getValue();
        if (date == null) {
            throw new IllegalArgumentException("Measurement date is required.");
        }
        String rawTime = measurementTimeField.getText() == null ? "" : measurementTimeField.getText().trim();
        if (rawTime.isBlank()) {
            throw new IllegalArgumentException("Measurement time is required (HH:mm).");
        }
        try {
            LocalTime time = LocalTime.parse(rawTime);
            return LocalDateTime.of(date, time);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Measurement time must use HH:mm format.");
        }
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

    private void clearForm() {
        systolicField.clear();
        diastolicField.clear();
        totalCholesterolField.clear();
        hdlField.clear();
        ldlField.clear();
        weightField.clear();
        measurementDatePicker.setValue(LocalDate.now());
        measurementTimeField.setText("08:00");
    }
}
