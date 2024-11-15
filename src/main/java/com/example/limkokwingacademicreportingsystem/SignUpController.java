package com.example.limkokwingacademicreportingsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class SignUpController {

    @FXML
    private TextField userIdField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private CheckBox termsCheckBox;

    // Database credentials
    private static final String URL = "jdbc:postgresql://localhost:5432/Limkokwing Academic Reporting System"; // Update with your database URL
    private static final String USER = "postgres"; // Your database username
    private static final String PASSWORD = "1234"; // Your database password

    @FXML
    private void handleSignUp() {
        String UserID = userIdField.getText().trim();
        String Name = nameField.getText().trim();
        String Email = emailField.getText().trim();
        String Password = passwordField.getText().trim();

        // Check if terms and conditions are agreed upon
        if (!termsCheckBox.isSelected()) {
            showAlert("Please agree to the terms and conditions.");
            return;
        }

        // Validate the password
        if (!isValidPassword(Password)) {
            showAlert("Password must contain letters, numbers, and symbols.");
            return;
        }

        // Save to the database and handle success/failure
        if (saveToDatabase(UserID,Name, Email, Password)) {
            showAlert("Account created successfully!");
            navigateToLogin(null); // Navigate to login after successful registration
        } else {
            showAlert("Failed to create account.");
        }
    }

    private boolean saveToDatabase(String UserID,String Name, String Email, String Password) {
        String sql = "INSERT INTO signup (UserID,Name, Email, Password) VALUES (?, ?, ?, ?)"; // Assuming 'signup' table

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, UserID);
            pstmt.setString(2, Name);
            pstmt.setString(3, Email);
            pstmt.setString(4, Password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidPassword(String password) {
        // Password should contain at least one letter, one digit, and one symbol
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    private void navigateToLogin(MouseEvent event) {
        try {
            // Load the Login screen (Login.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error while navigating to login screen.");
        }
    }

    @FXML
    private void navigateToLoginLabel(MouseEvent event) {
        navigateToLogin(event); // Use this method to navigate to the login page when the label is clicked
    }
}
