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
import com.txstate.bloodhound.ui.AddMeasurementView;
import com.txstate.bloodhound.ui.DashboardViewController;
import com.txstate.bloodhound.ui.DashboardView;
import com.txstate.bloodhound.ui.HistoryView;
import com.txstate.bloodhound.ui.LoginViewController;
import com.txstate.bloodhound.ui.LoginView;
import com.txstate.bloodhound.ui.RegisterViewController;
import com.txstate.bloodhound.ui.RegistrationView;
import com.txstate.bloodhound.model.User;
import com.txstate.bloodhound.util.DatabaseConfig;
import com.txstate.bloodhound.util.DatabaseConnectionManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX entry point for BloodHound 2.0.
 * <p>
 * This class wires placeholder layers and starts an empty JavaFX shell.
 */
public class BloodHoundApplication extends Application {
    private static final double APP_WIDTH = 960;
    private static final double APP_HEIGHT = 640;

    private AppState appState;
    private Stage primaryStage;
    private DashboardViewController dashboardController;
    private User currentDashboardUser;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

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
        ChartDataService chartDataService = new ChartDataService(measurementDao);
        AnalyticsService analyticsService = new AnalyticsService(measurementDao, chartDataService);
        appState = new AppState();

        // UI controllers.
        LoginViewController loginController = new LoginViewController(authService, appState);
        RegisterViewController registerController = new RegisterViewController(authService, appState);
        dashboardController = new DashboardViewController(appState, measurementService, analyticsService, chartDataService);

        loginView = new LoginView(loginController, this::showRegistrationScene, this::showDashboardScene);
        registrationView = new RegistrationView(registerController, this::showLoginScene, this::showLoginScene);

        // Keep controller referenced in scope for future dashboard feature wiring.
        if (dashboardController == null) {
            throw new IllegalStateException("Dashboard controller was not initialized.");
        }

        showLoginScene();
    }

    private LoginView loginView;
    private RegistrationView registrationView;

    private void showLoginScene() {
        if (loginView == null) {
            throw new IllegalStateException("Login view was not initialized.");
        }
        Scene scene = new Scene(loginView.build(), APP_WIDTH, APP_HEIGHT);
        primaryStage.setTitle("BloodHound 2.0 - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showRegistrationScene() {
        if (registrationView == null) {
            throw new IllegalStateException("Registration view was not initialized.");
        }
        Scene scene = new Scene(registrationView.build(), APP_WIDTH, APP_HEIGHT);
        primaryStage.setTitle("BloodHound 2.0 - Register");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showDashboardScene(User user) {
        currentDashboardUser = user;
        DashboardView dashboardView = new DashboardView(
                dashboardController,
                user,
                this::logoutToLogin,
                action -> showAddMeasurementScene(),
                action -> showHistoryScene());
        dashboardView.getLogoutButton().setOnAction(event -> logoutToLogin());
        Scene scene = new Scene(dashboardView.getRoot(), APP_WIDTH, APP_HEIGHT);
        primaryStage.setTitle("BloodHound 2.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAddMeasurementScene() {
        if (currentDashboardUser == null) {
            showLoginScene();
            return;
        }

        AddMeasurementView addMeasurementView = new AddMeasurementView(
                dashboardController,
                () -> showDashboardScene(currentDashboardUser),
                () -> showDashboardScene(currentDashboardUser));

        Scene scene = new Scene(addMeasurementView.getRoot(), APP_WIDTH, APP_HEIGHT);
        primaryStage.setTitle("BloodHound 2.0 - Add Measurement");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showHistoryScene() {
        if (currentDashboardUser == null) {
            showLoginScene();
            return;
        }

        HistoryView historyView = new HistoryView(
                dashboardController,
                () -> showDashboardScene(currentDashboardUser),
                () -> { });
        Scene scene = new Scene(historyView.getRoot(), APP_WIDTH, APP_HEIGHT);
        primaryStage.setTitle("BloodHound 2.0 - Measurement History");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void logoutToLogin() {
        appState.setCurrentUser(null);
        currentDashboardUser = null;
        showLoginScene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
