package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.User;
import com.txstate.bloodhound.util.OperationResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * JavaFX login screen for username/email authentication.
 */
public class LoginView {
    private final LoginViewController controller;
    private final Runnable onNavigateToRegistration;
    private final Consumer<User> onLoginSuccess;

    public LoginView(LoginViewController controller,
                     Runnable onNavigateToRegistration,
                     Consumer<User> onLoginSuccess) {
        this.controller = Objects.requireNonNull(controller, "controller must not be null");
        this.onNavigateToRegistration = Objects.requireNonNull(onNavigateToRegistration, "onNavigateToRegistration must not be null");
        this.onLoginSuccess = Objects.requireNonNull(onLoginSuccess, "onLoginSuccess must not be null");
    }

    /**
     * Builds the login scene root node.
     *
     * @return login UI root
     */
    public Parent build() {
        VBox root = new VBox(14);
        root.setPadding(new Insets(28));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f7f9fc;");

        Label title = new Label("BloodHound 2.0");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1f2a44;");

        Label subtitle = new Label("Sign in to your account");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #596684;");

        VBox card = new VBox(10);
        card.setMaxWidth(420);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #dde3f0; -fx-border-radius: 8;");

        TextField usernameOrEmailField = new TextField();
        usernameOrEmailField.setPromptText("Username or email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label errorLabel = new Label();
        errorLabel.setWrapText(true);
        errorLabel.setStyle("-fx-text-fill: #c62828; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle("-fx-background-color: #2f6fed; -fx-text-fill: white; -fx-font-weight: bold;");

        Button goToRegistrationButton = new Button("Create an account");
        goToRegistrationButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2f6fed;");

        HBox footer = new HBox(6);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.getChildren().addAll(new Label("No account?"), goToRegistrationButton);

        loginButton.setOnAction(event -> {
            OperationResult<User> result = controller.onLogin(
                    usernameOrEmailField.getText(),
                    passwordField.getText());
            if (result.isSuccess()) {
                showMessage(errorLabel, false, null);
                onLoginSuccess.accept(result.getData());
                return;
            }
            showMessage(errorLabel, true, formatErrors(result));
        });

        goToRegistrationButton.setOnAction(event -> onNavigateToRegistration.run());

        card.getChildren().addAll(
                labeledField("Username / Email", usernameOrEmailField),
                labeledField("Password", passwordField),
                errorLabel,
                loginButton,
                footer);

        root.getChildren().addAll(title, subtitle, card);
        VBox.setVgrow(card, Priority.NEVER);
        return root;
    }

    private Node labeledField(String labelText, TextField field) {
        VBox container = new VBox(4);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: #4b5a77;");
        field.setPrefHeight(36);
        container.getChildren().addAll(label, field);
        return container;
    }

    private void showMessage(Label label, boolean show, String message) {
        label.setVisible(show);
        label.setManaged(show);
        label.setText(show ? message : "");
    }

    private String formatErrors(OperationResult<?> result) {
        if (result.getErrors().isEmpty()) {
            return result.getMessage();
        }
        return String.join("\n", result.getErrors());
    }
}
