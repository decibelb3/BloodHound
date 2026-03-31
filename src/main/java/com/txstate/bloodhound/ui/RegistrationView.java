package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.RegistrationRequest;
import com.txstate.bloodhound.model.User;
import com.txstate.bloodhound.util.OperationResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * JavaFX registration screen for account creation.
 */
public class RegistrationView {
    private final RegisterViewController controller;
    private final Runnable onBackToLogin;
    private final Runnable onRegistrationComplete;

    private Label feedbackLabel;
    private TextField usernameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;

    public RegistrationView(RegisterViewController controller,
                            Runnable onBackToLogin,
                            Runnable onRegistrationComplete) {
        this.controller = controller;
        this.onBackToLogin = onBackToLogin;
        this.onRegistrationComplete = onRegistrationComplete;
    }

    /**
     * Builds and returns the registration root node.
     *
     * @return root JavaFX node for registration UI
     */
    public Parent build() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(28));
        root.setAlignment(Pos.CENTER);
        root.setMaxWidth(430);
        root.setStyle("-fx-background-color: #f6f8fb;");

        Label title = new Label("Create Account");
        title.setFont(Font.font("System", 24));

        Label subtitle = new Label("Register to start tracking your health metrics.");
        subtitle.setStyle("-fx-text-fill: #5f6c7b;");

        usernameField = new TextField();
        usernameField.setPromptText("Username");

        emailField = new TextField();
        emailField.setPromptText("Email");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm password");

        Button createAccountButton = new Button("Create Account");
        createAccountButton.setMaxWidth(Double.MAX_VALUE);
        createAccountButton.setOnAction(event -> handleCreateAccount());

        Hyperlink backToLoginLink = new Hyperlink("Back to login");
        backToLoginLink.setOnAction(event -> onBackToLogin.run());

        feedbackLabel = new Label();
        feedbackLabel.setWrapText(true);
        feedbackLabel.setTextFill(Color.FIREBRICK);

        root.getChildren().addAll(
                title,
                subtitle,
                usernameField,
                emailField,
                passwordField,
                confirmPasswordField,
                createAccountButton,
                backToLoginLink,
                feedbackLabel
        );
        return root;
    }

    private void handleCreateAccount() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!safeEquals(password, confirmPassword)) {
            showErrors("Registration failed.", java.util.List.of("Password and confirm password must match."));
            return;
        }

        RegistrationRequest request = new RegistrationRequest(
                usernameField.getText(),
                emailField.getText(),
                password
        );

        OperationResult<User> result = controller.register(request);
        if (!result.isSuccess()) {
            showErrors(result.getMessage(), result.getErrors());
            return;
        }

        feedbackLabel.setTextFill(Color.DARKGREEN);
        feedbackLabel.setText(result.getMessage());
        clearSensitiveFields();
        onRegistrationComplete.run();
    }

    private void showErrors(String message, java.util.List<String> errors) {
        feedbackLabel.setTextFill(Color.FIREBRICK);
        String details = errors == null || errors.isEmpty() ? "" : "\n- " + String.join("\n- ", errors);
        feedbackLabel.setText(message + details);
    }

    private void clearSensitiveFields() {
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private boolean safeEquals(String left, String right) {
        return left == null ? right == null : left.equals(right);
    }
}
