package com.example.limkokwingacademicreportingsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrincipalLecturerController {

    @FXML
    private ComboBox<String> classComboBox;

    @FXML
    private ComboBox<String> moduleComboBox;

    @FXML
    private TextArea challengesArea;

    @FXML
    private TextArea recommendationsArea;

    @FXML
    private Button submitReportButton;
    @FXML
    private Button backbtn;

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Limkokwing Academic Reporting System";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "1234";

    @FXML
    private void initialize() {
        // Fetch classes and modules from the database
        loadClassesAndModules();

        // Set action for the submit report button
        submitReportButton.setOnAction(event -> submitReport());
    }

    private void loadClassesAndModules() {
        // Clear the ComboBoxes before adding new items
        classComboBox.getItems().clear();
        moduleComboBox.getItems().clear();

        // Fetch class data from database
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String classSql = "SELECT name FROM class";
            PreparedStatement classStatement = connection.prepareStatement(classSql);
            ResultSet classResultSet = classStatement.executeQuery();
            while (classResultSet.next()) {
                classComboBox.getItems().add(classResultSet.getString("name"));
            }

            // Fetch module data from database
            String moduleSql = "SELECT module_name FROM module";
            PreparedStatement moduleStatement = connection.prepareStatement(moduleSql);
            ResultSet moduleResultSet = moduleStatement.executeQuery();
            while (moduleResultSet.next()) {
                moduleComboBox.getItems().add(moduleResultSet.getString("module_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while fetching data from the database.");
        }
    }

    private void submitReport() {
        String selectedClass = classComboBox.getValue();
        String selectedModule = moduleComboBox.getValue();
        String challenges = challengesArea.getText();
        String recommendations = recommendationsArea.getText();

        // Validate input
        if (selectedClass == null || selectedModule == null || challenges.trim().isEmpty() || recommendations.trim().isEmpty()) {
            showAlert("Input Error", "All fields must be filled before submitting the report.");
            return;
        }

        // Submit the report to the database
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO principalLecturerreports (class_name, module_name, challenges, recommendations) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, selectedClass);
            statement.setString(2, selectedModule);
            statement.setString(3, challenges);
            statement.setString(4, recommendations);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Report submitted successfully!");
                clearFields();
            } else {
                showAlert("Error", "Failed to submit the report.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while submitting the report.");
        }
    }

    @FXML
    public void goBack() {
        // Load the Admin FXML screen and close the current window
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/limkokwingacademicreportingsystem/Homepage.fxml"));
            AnchorPane adminScreen = loader.load();
            Stage stage = (Stage) backbtn.getScene().getWindow();
            stage.setScene(new Scene(adminScreen));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void clearFields() {
        // Clear fields after submission
        challengesArea.clear();
        recommendationsArea.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
