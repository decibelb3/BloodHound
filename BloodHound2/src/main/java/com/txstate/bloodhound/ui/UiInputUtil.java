package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.util.OperationResult;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared UI parsing and formatting helpers used by JavaFX views.
 */
public final class UiInputUtil {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter TIME_ONLY_FORMATTER = TIME_FORMATTER;

    private UiInputUtil() {
    }

    /**
     * Parses an optional integer field.
     *
     * @param raw raw text value
     * @param fieldName display field label
     * @return parsed integer or null when blank
     */
    public static Integer parseOptionalInteger(String raw, String fieldName) {
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

    /**
     * Parses an optional double field.
     *
     * @param raw raw text value
     * @param fieldName display field label
     * @return parsed double or null when blank
     */
    public static Double parseOptionalDouble(String raw, String fieldName) {
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

    /**
     * Parses a date-time pair from UI controls.
     *
     * @param date selected date
     * @param timeText selected time text in HH:mm
     * @param labelPrefix field label prefix, such as "start"
     * @return parsed date-time
     */
    public static LocalDateTime parseDateTime(LocalDate date, String timeText, String labelPrefix) {
        if (date == null) {
            throw new IllegalArgumentException("Select a " + labelPrefix + " date.");
        }
        String rawTime = timeText == null ? "" : timeText.trim();
        if (rawTime.isBlank()) {
            throw new IllegalArgumentException("Select a " + labelPrefix + " time (HH:mm).");
        }
        try {
            LocalTime time = LocalTime.parse(rawTime, TIME_FORMATTER);
            return LocalDateTime.of(date, time);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid " + labelPrefix + " time. Use HH:mm.");
        }
    }

    /**
     * Parses a full date-time range and validates chronological order.
     *
     * @param startDate start date
     * @param startTime start time text (HH:mm)
     * @param endDate end date
     * @param endTime end time text (HH:mm)
     * @return result containing range values or errors
     */
    public static OperationResult<DateTimeRange> parseDateTimeRange(LocalDate startDate,
                                                                    String startTime,
                                                                    LocalDate endDate,
                                                                    String endTime) {
        if (startDate == null || endDate == null) {
            return OperationResult.failure("Invalid date range.", List.of("Select both start and end dates."));
        }
        try {
            LocalDateTime start = parseDateTime(startDate, startTime, "start");
            LocalDateTime end = parseDateTime(endDate, endTime, "end");
            if (end.isBefore(start)) {
                return OperationResult.failure("Invalid date range.",
                        List.of("End date/time cannot be before start date/time."));
            }
            return OperationResult.success("Date range selected.", new DateTimeRange(start, end));
        } catch (IllegalArgumentException exception) {
            return OperationResult.failure("Invalid date range.", List.of(exception.getMessage()));
        }
    }

    /**
     * Backward-compatible alias for parsing a date-time range from values.
     *
     * @param startDate start date
     * @param startTime start time text
     * @param endDate end date
     * @param endTime end time text
     * @return parsed range result
     */
    public static OperationResult<DateTimeRange> resolveDateTimeRange(LocalDate startDate,
                                                                      String startTime,
                                                                      LocalDate endDate,
                                                                      String endTime) {
        return parseDateTimeRange(startDate, startTime, endDate, endTime);
    }

    /**
     * Parses a date-time range from JavaFX input controls.
     *
     * @param startDatePicker start date picker
     * @param startTimeField start time field
     * @param endDatePicker end date picker
     * @param endTimeField end time field
     * @return parsed range result
     */
    public static OperationResult<DateTimeRangeValue> resolveDateTimeRange(DatePicker startDatePicker,
                                                                            TextField startTimeField,
                                                                            DatePicker endDatePicker,
                                                                            TextField endTimeField) {
        OperationResult<DateTimeRange> result = parseDateTimeRange(
                startDatePicker.getValue(),
                startTimeField.getText(),
                endDatePicker.getValue(),
                endTimeField.getText());
        if (!result.isSuccess() || result.getData() == null) {
            return OperationResult.failure(result.getMessage(), result.getErrors());
        }
        DateTimeRange range = result.getData();
        return OperationResult.success(result.getMessage(),
                new DateTimeRangeValue(range.startInclusive(), range.endInclusive()));
    }

    /**
     * Backward-compatible alias for date-range parsing.
     *
     * @param startDate start date
     * @param startTime start time
     * @param endDate end date
     * @param endTime end time
     * @return parsed range result
     */
    public static OperationResult<DateRangeValue> resolveDateRange(LocalDate startDate,
                                                                   String startTime,
                                                                   LocalDate endDate,
                                                                   String endTime) {
        OperationResult<DateTimeRange> result = parseDateTimeRange(startDate, startTime, endDate, endTime);
        if (!result.isSuccess() || result.getData() == null) {
            return OperationResult.failure(result.getMessage(), result.getErrors());
        }
        DateTimeRange range = result.getData();
        return OperationResult.success(result.getMessage(),
                new DateRangeValue(range.startInclusive(), range.endInclusive()));
    }

    /**
     * Parses a required date-time value and returns a clear validation message on failure.
     *
     * @param date selected date
     * @param timeText selected time
     * @param labelPrefix label prefix, such as "start" or "measurement"
     * @return parsed date-time
     */
    public static LocalDateTime parseRequiredDateTime(LocalDate date, String timeText, String labelPrefix) {
        return parseDateTime(date, timeText, labelPrefix);
    }

    /**
     * Backward-compatible alias for required date-time parsing.
     *
     * @param date selected date
     * @param timeText selected time
     * @param labelPrefix label prefix
     * @return parsed date-time
     */
    public static LocalDateTime resolveDateTime(LocalDate date, String timeText, String labelPrefix) {
        return parseDateTime(date, timeText, labelPrefix);
    }

    /**
     * Parses a required date-time value using custom required-field messages.
     *
     * @param date selected date
     * @param timeText selected time text
     * @param missingDateMessage error message when date is missing
     * @param missingTimeMessage error message when time is missing
     * @return parsed date-time
     */
    public static LocalDateTime resolveRequiredDateTime(LocalDate date,
                                                        String timeText,
                                                        String missingDateMessage,
                                                        String missingTimeMessage) {
        if (date == null) {
            throw new IllegalArgumentException(missingDateMessage);
        }
        String rawTime = timeText == null ? "" : timeText.trim();
        if (rawTime.isBlank()) {
            throw new IllegalArgumentException(missingTimeMessage);
        }
        try {
            LocalTime time = LocalTime.parse(rawTime, TIME_FORMATTER);
            return LocalDateTime.of(date, time);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Measurement time must use HH:mm format.");
        }
    }

    /**
     * Applies a stored date-time range to input controls.
     *
     * @param start start date-time
     * @param end end date-time
     * @param startDatePicker start date picker
     * @param startTimeField start time field
     * @param endDatePicker end date picker
     * @param endTimeField end time field
     * @param defaultStartTime default start time text when range is empty
     * @param defaultEndTime default end time text when range is empty
     */
    public static void applyDateTimeRangeToInputs(LocalDateTime start,
                                                  LocalDateTime end,
                                                  DatePicker startDatePicker,
                                                  TextField startTimeField,
                                                  DatePicker endDatePicker,
                                                  TextField endTimeField,
                                                  String defaultStartTime,
                                                  String defaultEndTime) {
        if (start != null && end != null) {
            startDatePicker.setValue(start.toLocalDate());
            endDatePicker.setValue(end.toLocalDate());
            startTimeField.setText(start.toLocalTime().format(TIME_FORMATTER));
            endTimeField.setText(end.toLocalTime().format(TIME_FORMATTER));
            return;
        }
        startTimeField.setText(defaultStartTime);
        endTimeField.setText(defaultEndTime);
    }

    /**
     * Applies app-state filter values to filter controls.
     *
     * @param appState shared application state
     * @param startDatePicker start date picker
     * @param startTimeField start time field
     * @param endDatePicker end date picker
     * @param endTimeField end time field
     * @param timeFormatter formatter for time fields
     */
    public static void applyStoredDateRangeToInputs(AppState appState,
                                                    DatePicker startDatePicker,
                                                    TextField startTimeField,
                                                    DatePicker endDatePicker,
                                                    TextField endTimeField,
                                                    DateTimeFormatter timeFormatter) {
        LocalDateTime start = appState.getFilterStartDateTime();
        LocalDateTime end = appState.getFilterEndDateTime();
        if (start != null && end != null) {
            startDatePicker.setValue(start.toLocalDate());
            endDatePicker.setValue(end.toLocalDate());
            startTimeField.setText(start.toLocalTime().format(timeFormatter));
            endTimeField.setText(end.toLocalTime().format(timeFormatter));
            return;
        }
        resetTimeRangeInputs(startTimeField, endTimeField);
    }

    /**
     * Resets time-range text fields to default values.
     *
     * @param startTimeField start time field
     * @param endTimeField end time field
     */
    public static void resetTimeRangeInputs(TextField startTimeField, TextField endTimeField) {
        startTimeField.setText("00:00");
        endTimeField.setText("23:59");
    }

    /**
     * Backward-compatible alias for resetting time range fields.
     *
     * @param startTimeField start time field
     * @param endTimeField end time field
     */
    public static void resetTimeFields(TextField startTimeField, TextField endTimeField) {
        resetTimeRangeInputs(startTimeField, endTimeField);
    }

    /**
     * Formats operation-level errors into one readable message.
     *
     * @param result result wrapper
     * @param fallback fallback message when result has no details
     * @return rendered message
     */
    public static String formatOperationErrors(OperationResult<?> result, String fallback) {
        List<String> messages = new ArrayList<>();
        if (result.getMessage() != null && !result.getMessage().isBlank()) {
            messages.add(result.getMessage());
        }
        if (result.getErrors() != null && !result.getErrors().isEmpty()) {
            messages.addAll(result.getErrors());
        }
        if (messages.isEmpty()) {
            return fallback;
        }
        return String.join(" | ", messages);
    }

    /**
     * Formats operation errors with a default fallback.
     *
     * @param result result wrapper
     * @return rendered message
     */
    public static String formatOperationErrors(OperationResult<?> result) {
        return formatOperationErrors(result, "Request failed.");
    }

    /**
     * Lightweight range value object for UI filtering.
     *
     * @param startInclusive range start
     * @param endInclusive range end
     */
    public record DateTimeRange(LocalDateTime startInclusive, LocalDateTime endInclusive) {
        public LocalDateTime start() {
            return startInclusive;
        }

        public LocalDateTime end() {
            return endInclusive;
        }
    }

    /**
     * Backward-compatible date-time range alias used by existing views.
     *
     * @param start range start
     * @param end range end
     */
    public record DateTimeRangeValue(LocalDateTime start, LocalDateTime end) {
    }

    /**
     * Backward-compatible date-time range alias used by dashboard view.
     *
     * @param start range start
     * @param end range end
     */
    public record DateRangeValue(LocalDateTime start, LocalDateTime end) {
    }
}
