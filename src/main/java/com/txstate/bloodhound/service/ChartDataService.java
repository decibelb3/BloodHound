package com.txstate.bloodhound.service;

import javafx.scene.chart.XYChart;

/**
 * Prepares chart-ready series data for JavaFX trend visualizations.
 */
public class ChartDataService {
    private final MeasurementService measurementService;

    public ChartDataService(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    /**
     * Builds trend series for systolic blood pressure.
     *
     * @param userId owner user identifier
     * @return JavaFX series placeholder
     */
    public XYChart.Series<String, Number> buildSystolicSeries(Long userId) {
        // TODO: Build sorted date/time trend series from measurements.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Builds trend series for diastolic blood pressure.
     *
     * @param userId owner user identifier
     * @return JavaFX series placeholder
     */
    public XYChart.Series<String, Number> buildDiastolicSeries(Long userId) {
        // TODO: Build sorted date/time trend series from measurements.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Builds trend series for total cholesterol.
     *
     * @param userId owner user identifier
     * @return JavaFX series placeholder
     */
    public XYChart.Series<String, Number> buildTotalCholesterolSeries(Long userId) {
        // TODO: Build sorted date/time trend series from measurements.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Builds trend series for HDL.
     *
     * @param userId owner user identifier
     * @return JavaFX series placeholder
     */
    public XYChart.Series<String, Number> buildHdlSeries(Long userId) {
        // TODO: Build sorted date/time trend series from measurements.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Builds trend series for LDL.
     *
     * @param userId owner user identifier
     * @return JavaFX series placeholder
     */
    public XYChart.Series<String, Number> buildLdlSeries(Long userId) {
        // TODO: Build sorted date/time trend series from measurements.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Builds trend series for weight.
     *
     * @param userId owner user identifier
     * @return JavaFX series placeholder
     */
    public XYChart.Series<String, Number> buildWeightSeries(Long userId) {
        // TODO: Build sorted date/time trend series from measurements.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public MeasurementService getMeasurementService() {
        return measurementService;
    }
}
