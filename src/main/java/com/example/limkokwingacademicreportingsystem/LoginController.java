package com.example.limkokwingacademicreportingsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

public class LoginController {

    @FXML
    private TextField userIdField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;

    private Connection conn;

    // Database credentials
    private static final String URL = "jdbc:postgresql://localhost:5432/Limkokwing Academic Reporting System";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public void initialize() {
        // Initialize connection here if needed
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database connection failed.");
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String UserID = userIdField.getText().trim();
        String Email = emailField.getText().trim();
        String Password = passwordField.getText().trim();

        if (validateLogin(UserID, Email, Password)) {
            // Success: Proceed to Homepage screen
            navigateToHomepage();
        } else {
            showError("Invalid login credentials. Please try again.");
        }
    }

    private boolean validateLogin(String UserID, String Email, String Password) {
        String query = "SELECT * FROM signup WHERE UserID = ? AND Email = ? AND Password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, UserID);
            stmt.setString(2, Email);
            stmt.setString(3, Password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error while querying the database.");
            return false;
        }
    }

    private void navigateToHomepage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Homepage.fxml"));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load the Homepage.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void navigateToSignUp(ActionEvent event) {
        System.out.println("Navigate to Sign-Up screen.");
    }
}
