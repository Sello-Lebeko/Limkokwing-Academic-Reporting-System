package com.example.limkokwingacademicreportingsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

public class AddLecturerController {

    @FXML
    private TextField lecturerIdField;
    @FXML
    private TextField lecturerNameField;
    @FXML
    private TextField lecturerEmailField;
    @FXML
    private TextField lecturerGenderField;

    @FXML
    private Button addLecturerButton;

    @FXML
    private Button resetButton;

    @FXML
    private TableView<Lecturer> lecturerTable;

    @FXML
    private TableColumn<Lecturer, String> lecturerIdColumn;

    @FXML
    private TableColumn<Lecturer, String> lecturerNameColumn;

    @FXML
    private TableColumn<Lecturer, String> lecturerEmailColumn;
    @FXML
    private Button back_btn;

    @FXML
    private TableColumn<Lecturer, String> lecturerGenderColumn;

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Limkokwing Academic Reporting System";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "1234";

    // ObservableList to hold lecturer data
    private ObservableList<Lecturer> lecturerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize the TableView columns
        lecturerIdColumn.setCellValueFactory(cellData -> cellData.getValue().lecturerIdProperty());
        lecturerNameColumn.setCellValueFactory(cellData -> cellData.getValue().lecturerNameProperty());
        lecturerEmailColumn.setCellValueFactory(cellData -> cellData.getValue().lecturerEmailProperty());
        lecturerGenderColumn.setCellValueFactory(cellData -> cellData.getValue().lecturerGenderProperty());

        // Load the existing lecturers from the database
        loadLecturersFromDatabase();


    }

    @FXML
    public void addLecturer() {
        String lecturerId = lecturerIdField.getText();
        String lecturerName = lecturerNameField.getText();
        String lecturerEmail = lecturerEmailField.getText();
        String lecturerGender = lecturerGenderField.getText();

        // Validation check
        if (lecturerId.isEmpty() || lecturerName.isEmpty() || lecturerEmail.isEmpty() || lecturerGender.isEmpty()) {
            showAlert("Input Error", "All fields must be filled.");
            return;
        }

        // Connect to the database and insert the lecturer data
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO lecturers (id, name, email, gender) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, lecturerId);
            statement.setString(2, lecturerName);
            statement.setString(3, lecturerEmail);
            statement.setString(4, lecturerGender);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Lecturer added successfully!");
                loadLecturersFromDatabase();
                clearFields();
            } else {
                showAlert("Error", "Failed to add lecturer.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while connecting to the database.");
        }
    }

    private void clearFields() {
        lecturerIdField.clear();
        lecturerNameField.clear();
        lecturerEmailField.clear();
        lecturerGenderField.clear();
    }

    private void loadLecturersFromDatabase() {
        lecturerList.clear();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM lecturers";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String lecturerId = resultSet.getString("id");
                String lecturerName = resultSet.getString("name");
                String lecturerEmail = resultSet.getString("email");
                String lecturerGender = resultSet.getString("gender");
                lecturerList.add(new Lecturer(lecturerId, lecturerName, lecturerEmail, lecturerGender));
            }

            lecturerTable.setItems(lecturerList);
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

    @FXML
    public void deleteLecturer() {
        String lecturerId = lecturerIdField.getText();
        if (lecturerId.isEmpty()) {
            showAlert("Input Error", "Please provide a lecturer ID to delete.");
            return;
        }

        // Perform the deletion remove from the database
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM lecturer WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, lecturerId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "Lecturer deleted successfully!");
                loadLecturersFromDatabase();
                clearFields();
            } else {
                showAlert("Error", "Lecturer not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while deleting the lecturer.");
        }
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




    public static class Lecturer {
        private final SimpleStringProperty lecturerId;
        private final SimpleStringProperty lecturerName;
        private final SimpleStringProperty lecturerEmail;
        private final SimpleStringProperty lecturerGender;

        public Lecturer(String lecturerId, String lecturerName, String lecturerEmail, String lecturerGender) {
            this.lecturerId = new SimpleStringProperty(lecturerId);
            this.lecturerName = new SimpleStringProperty(lecturerName);
            this.lecturerEmail = new SimpleStringProperty(lecturerEmail);
            this.lecturerGender = new SimpleStringProperty(lecturerGender);
        }

        public String getLecturerId() {
            return lecturerId.get();
        }

        public String getLecturerName() {
            return lecturerName.get();
        }

        public String getLecturerEmail() {
            return lecturerEmail.get();
        }

        public String getLecturerGender() {
            return lecturerGender.get();
        }

        public SimpleStringProperty lecturerIdProperty() {
            return lecturerId;
        }

        public SimpleStringProperty lecturerNameProperty() {
            return lecturerName;
        }

        public SimpleStringProperty lecturerEmailProperty() {
            return lecturerEmail;
        }

        public SimpleStringProperty lecturerGenderProperty() {
            return lecturerGender;
        }
    }
}
