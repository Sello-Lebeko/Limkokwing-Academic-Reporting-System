package com.example.limkokwingacademicreportingsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddSemesterController {

    @FXML
    private TextField add_semester;

    @FXML
    private Button add_semester_btn;

    @FXML
    private Button reset_btn;
    @FXML
    private Button backbtn;

    @FXML
    private TableView<Semester> semesterTable;

    @FXML
    private TableColumn<Semester, String> semesterNameColumn;

    @FXML
    private TableColumn<Semester, String> yearColumn;

    @FXML
    private ComboBox<String> yearComboBox; // ComboBox for selecting year

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Limkokwing Academic Reporting System"; // Replace with your database URL
    private static final String DB_USER = "postgres"; // Replace with your database username
    private static final String DB_PASSWORD = "1234"; // Replace with your database password

    // ObservableList to hold semester data
    private ObservableList<Semester> semesterList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize the TableView columns
        semesterNameColumn.setCellValueFactory(cellData -> cellData.getValue().semesterNameProperty());
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().yearProperty());

        // Load the existing semesters from the database
        loadSemestersFromDatabase();

        // Populate the ComboBox with year options (can also be dynamic)
        yearComboBox.setItems(FXCollections.observableArrayList("Year 1", "Year 2", "Year 3", "Year 4"));
    }

    @FXML
    public void addSemester() {
        String semester = add_semester.getText();
        String selectedYear = yearComboBox.getSelectionModel().getSelectedItem();

        // Validation checks
        if (semester.isEmpty() || selectedYear == null) {
            showAlert("Input Error", "Both year and semester must be selected.");
            return;
        }

        // Connect to the database and insert the semester data
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO semester (semester_name, year) VALUES (?, ?)"; // Replace with your actual table and column names
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, semester); // Set the semester name
            statement.setString(2, selectedYear); // Set the selected year

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Semester added successfully!");
                loadSemestersFromDatabase(); // Reload the table with the new semester
                clearFields(); // Clear the text field and ComboBox after successful insertion
            } else {
                showAlert("Error", "Failed to add semester.");
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
            Stage stage = (Stage) backbtn.getScene().getWindow();
            stage.setScene(new Scene(adminScreen));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading the Admin screen.");
        }
    }

    @FXML
    public void resetFields() {
        clearFields();
    }

    private void clearFields() {
        add_semester.clear();
        yearComboBox.getSelectionModel().clearSelection(); // Clear the ComboBox selection
    }

    private void loadSemestersFromDatabase() {
        semesterList.clear(); // Clear existing data

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM semester"; // Replace with your actual table name
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String semesterName = resultSet.getString("semester_name"); // Assuming your table has a "semester_name" column
                String year = resultSet.getString("year"); // Assuming you have a "year" column
                semesterList.add(new Semester(semesterName, year));
            }

            semesterTable.setItems(semesterList); // Set the items for the TableView
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while fetching data from the database.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Semester {
        private final SimpleStringProperty semesterName;
        private final SimpleStringProperty year;

        public Semester(String semesterName, String year) {
            this.semesterName = new SimpleStringProperty(semesterName);
            this.year = new SimpleStringProperty(year);
        }

        // Removed the getter methods as they're unnecessary for binding
        public SimpleStringProperty semesterNameProperty() {
            return semesterName;
        }

        public SimpleStringProperty yearProperty() {
            return year;
        }
    }
}
