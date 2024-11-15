package com.example.limkokwingacademicreportingsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddAcademicYearController {

    @FXML
    private ComboBox<String> academicYearComboBox;  // ComboBox to select academic year
    @FXML
    private Label resultLabel;  // Label to display success or error messages

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Limkokwing Academic Reporting System";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "1234";

    @FXML
    public void initialize() {
        // Populate the ComboBox with sample academic years (or retrieve from database if needed)
        academicYearComboBox.getItems().addAll("2024/2025", "2025/2026", "2026/2027", "2027/2028");

        // Set default value
        academicYearComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void addAcademicYear() {
        String selectedYear = academicYearComboBox.getValue();

        // Validation: Check if a valid academic year is selected
        if (selectedYear == null || selectedYear.trim().isEmpty()) {
            showAlert("Input Error", "Please select a valid academic year.");
            return;
        }

        // Connect to the database and insert the academic year
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO academic_years (year) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, selectedYear);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                resultLabel.setText("Academic Year " + selectedYear + " added successfully.");
                showAlert("Success", "Academic Year added successfully!");
            } else {
                resultLabel.setText("Failed to add academic year.");
                showAlert("Error", "Failed to add academic year.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while connecting to the database.");
        }
    }

    @FXML
    public void goBack() {
        // Load the Admin FXML screen and close the current window
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/limkokwingacademicreportingsystem/Admin.fxml"));
            AnchorPane adminScreen = loader.load();
            Stage stage = (Stage) resultLabel.getScene().getWindow();
            stage.setScene(new Scene(adminScreen));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading the Admin screen.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
