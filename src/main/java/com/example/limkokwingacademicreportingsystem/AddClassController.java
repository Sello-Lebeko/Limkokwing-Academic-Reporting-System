package com.example.limkokwingacademicreportingsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddClassController {

    @FXML
    private Button add_btn;

    @FXML
    private TextField add_class_text_field;

    @FXML
    private Button back_btn;

    @FXML
    private Button reset;

    @FXML
    private TableView<ClassData> classesTable; // TableView to display classes

    @FXML
    private TableColumn<ClassData, String> classNameColumn; // TableColumn to display class names

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Limkokwing Academic Reporting System"; // Replace with your database URL
    private static final String DB_USER = "postgres"; // Replace with your database username
    private static final String DB_PASSWORD = "1234"; // Replace with your database password

    // This method is triggered when the Add button is clicked
    @FXML
    public void addClass() {
        String className = add_class_text_field.getText();

        if (className == null || className.trim().isEmpty()) {
            showAlert("Input Error", "Please enter a valid class name.");
            return;
        }

        // Connect to the database and insert data
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO class (name) VALUES (?)"; // Replace "classes" and "class_name" with your actual table and column names
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, className);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Class added successfully!");
                clearFields(); // Clear the text field after successful insertion
                loadClasses(); // Reload the table to reflect the new class
            } else {
                showAlert("Error", "Failed to add class.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while connecting to the database.");
        }
    }

    // This method is triggered when the Reset button is clicked
    @FXML
    public void resetFields() {
        add_class_text_field.clear();
    }

    @FXML
    public void goBack() {
        // Load the Admin FXML screen and close the current window
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/limkokwingacademicreportingsystem/Admin.fxml"));
            AnchorPane adminScreen = loader.load();
            Stage stage = (Stage) back_btn.getScene().getWindow();
            stage.setScene(new Scene(adminScreen));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading the Admin screen.");
        }
    }

    // Show an alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Clear the input fields
    private void clearFields() {
        add_class_text_field.clear();
    }

    // Method to load classes into the TableView
    private void loadClasses() {
        ObservableList<ClassData> classList = FXCollections.observableArrayList();

        // Fetch data from the database
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT name FROM class"; // Replace with your actual SQL query
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String className = resultSet.getString("name");
                classList.add(new ClassData(className)); // Add each class to the list
            }

            // Set the data in the TableView
            classesTable.setItems(classList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while fetching data from the database.");
        }
    }

    // Initialize the TableView columns and load data
    @FXML
    private void initialize() {
        // Initialize TableColumn with the appropriate property
        classNameColumn.setCellValueFactory(new PropertyValueFactory<>("className"));

        // Load the classes from the database into the TableView
        loadClasses();
    }

    // Static inner class for ClassData
    public static class ClassData {

        private String className;

        public ClassData(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }
}
