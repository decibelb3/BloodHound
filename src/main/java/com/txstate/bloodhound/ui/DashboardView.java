package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Simple post-login dashboard shell.
 */
public class DashboardView {
    private final BorderPane root;
    private final Button logoutButton;

    public DashboardView(User user) {
        this.root = new BorderPane();
        this.logoutButton = new Button("Log Out");
        build(user);
    }

    public BorderPane getRoot() {
        return root;
    }

    public Button getLogoutButton() {
        return logoutButton;
    }

    private void build(User user) {
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f4f7fb, #ffffff);");

        Label title = new Label("BloodHound 2.0 Dashboard");
        title.setFont(Font.font(24));

        Label welcome = new Label("Welcome, " + (user == null ? "User" : user.getUsername()) + ".");
        welcome.setStyle("-fx-text-fill: #1f2937; -fx-font-size: 14;");

        Label info = new Label("Login successful. Dashboard widgets and charts can be attached here.");
        info.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 13;");

        VBox centerCard = new VBox(10, title, welcome, info);
        centerCard.setPadding(new Insets(20));
        centerCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; "
                + "-fx-border-color: #dbe4ee; -fx-border-radius: 12;");

        HBox footer = new HBox(logoutButton);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 0, 0, 0));

        root.setCenter(centerCard);
        root.setBottom(footer);
    }
}
