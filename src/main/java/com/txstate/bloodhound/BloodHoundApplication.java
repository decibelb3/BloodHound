package com.txstate.bloodhound;

import com.txstate.bloodhound.dao.HealthMeasurementDao;
import com.txstate.bloodhound.dao.HealthMeasurementDaoImpl;
import com.txstate.bloodhound.dao.UserDaoImpl;
import com.txstate.bloodhound.dao.UserDao;
import com.txstate.bloodhound.service.AnalyticsService;
import com.txstate.bloodhound.service.AuthService;
import com.txstate.bloodhound.service.ChartDataService;
import com.txstate.bloodhound.service.MeasurementService;
import com.txstate.bloodhound.ui.AppState;
import com.txstate.bloodhound.ui.DashboardViewController;
import com.txstate.bloodhound.ui.LoginViewController;
import com.txstate.bloodhound.ui.RegisterViewController;
import com.txstate.bloodhound.util.DatabaseConfig;
import com.txstate.bloodhound.util.DatabaseConnectionManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX entry point for BloodHound 2.0.
 * <p>
 * This class wires placeholder layers and starts an empty JavaFX shell.
 */
public class BloodHoundApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Centralized connection settings for DAO construction.
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_USERNAME,
                DatabaseConfig.DB_PASSWORD);

        // DAO and service wiring placeholders.
        UserDao userDao = new UserDaoImpl(connectionManager);
        HealthMeasurementDao measurementDao = new HealthMeasurementDaoImpl(connectionManager);

        AuthService authService = new AuthService(userDao);
        MeasurementService measurementService = new MeasurementService(measurementDao);
        ChartDataService chartDataService = new ChartDataService(measurementService);
        AnalyticsService analyticsService = new AnalyticsService(measurementDao, chartDataService);
        AppState appState = new AppState();

        // UI controller placeholders.
        LoginViewController loginController = new LoginViewController(authService, appState);
        RegisterViewController registerController = new RegisterViewController(authService, appState);
        DashboardViewController dashboardController =
                new DashboardViewController(appState, measurementService, analyticsService, chartDataService);

        // Suppress unused warnings while shell is intentionally minimal.
        if (loginController == null || registerController == null || dashboardController == null) {
            throw new IllegalStateException("UI controllers failed to initialize.");
        }

        VBox root = new VBox(12);
        root.getChildren().addAll(
                new Label("BloodHound 2.0"),
                new Label("JavaFX multi-user shell initialized."),
                new Label("Login/register/dashboard flows are scaffolded and ready for implementation."));
        root.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(root, 840, 520);
        primaryStage.setTitle("BloodHound 2.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
