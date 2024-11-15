package com.example.limkokwingacademicreportingsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AssignRoleController {

    @FXML
    private TableView<AssignedRole> assignRoleTable;

    @FXML
    private TableColumn<AssignedRole, String> colLecturerName;

    @FXML
    private TableColumn<AssignedRole, String> colRole;

    @FXML
    private ComboBox<String> selectLecturerComboBox;

    @FXML
    private ComboBox<String> assignRoleComboBox;

    @FXML
    private Button assign_btn;

    @FXML
    private Button reset_btn;

    @FXML
    private Button backbtn;

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Limkokwing Academic Reporting System";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "1234";

    private ObservableList<AssignedRole> assignedRoleList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colLecturerName.setCellValueFactory(cellData -> cellData.getValue().lecturerNameProperty());
        colRole.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        loadAssignedRolesFromDatabase();
        loadLecturersAndRolesFromDatabase();
    }

    @FXML
    public void assignRole() {
        String lecturerName = selectLecturerComboBox.getSelectionModel().getSelectedItem();
        String role = assignRoleComboBox.getSelectionModel().getSelectedItem();

        if (lecturerName == null || role == null) {
            showAlert("Input Error", "Both lecturer and role must be selected.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO assigned_roles (lecturer_name, role) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, lecturerName);
            statement.setString(2, role);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Role assigned successfully!");
                loadAssignedRolesFromDatabase();
                clearFields();
            } else {
                showAlert("Error", "Failed to assign role.");
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
        selectLecturerComboBox.getSelectionModel().clearSelection();
        assignRoleComboBox.getSelectionModel().clearSelection();
    }

    private void loadAssignedRolesFromDatabase() {
        assignedRoleList.clear();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM assigned_roles";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String lecturerName = resultSet.getString("lecturer_name");
                String role = resultSet.getString("role");
                assignedRoleList.add(new AssignedRole(lecturerName, role));
            }

            assignRoleTable.setItems(assignedRoleList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while fetching data from the database.");
        }
    }

    private void loadLecturersAndRolesFromDatabase() {
        ObservableList<String> lecturers = FXCollections.observableArrayList();
        ObservableList<String> roles = FXCollections.observableArrayList("Principal Lecturer", "Lecturer");

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String lecturerSql = "SELECT name FROM lecturers";
            PreparedStatement lecturerStatement = connection.prepareStatement(lecturerSql);
            ResultSet lecturerResultSet = lecturerStatement.executeQuery();
            while (lecturerResultSet.next()) {
                lecturers.add(lecturerResultSet.getString("name"));
            }

            selectLecturerComboBox.setItems(lecturers);
            assignRoleComboBox.setItems(roles);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while fetching lecturer and role data.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class AssignedRole {
        private final SimpleStringProperty lecturerName;
        private final SimpleStringProperty role;

        public AssignedRole(String lecturerName, String role) {
            this.lecturerName = new SimpleStringProperty(lecturerName);
            this.role = new SimpleStringProperty(role);
        }

        public SimpleStringProperty lecturerNameProperty() {
            return lecturerName;
        }

        public SimpleStringProperty roleProperty() {
            return role;
        }
    }
}
