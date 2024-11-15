package com.example.limkokwingacademicreportingsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.*;

public class AssignModuleController {

    @FXML
    private TableView<AssignedModule> assignedModuleTable;

    @FXML
    private TableColumn<AssignedModule, String> lecturerNameColumn;

    @FXML
    private TableColumn<AssignedModule, String> moduleCodeColumn;

    @FXML
    private TableColumn<AssignedModule, String> moduleNameColumn;

    @FXML
    private ComboBox<String> lecturerNameComboBox;

    @FXML
    private ComboBox<String> moduleCodeComboBox;

    @FXML
    private ComboBox<String> moduleNameComboBox;

    @FXML
    private Button assignButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button back_btn;

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Limkokwing Academic Reporting System";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "1234";

    // ObservableList to hold assigned module data
    private ObservableList<AssignedModule> assignedModuleList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize the TableView columns
        lecturerNameColumn.setCellValueFactory(cellData -> cellData.getValue().lecturerNameProperty());
        moduleCodeColumn.setCellValueFactory(cellData -> cellData.getValue().moduleCodeProperty());
        moduleNameColumn.setCellValueFactory(cellData -> cellData.getValue().moduleNameProperty());

        // Load assigned modules from the database
        loadAssignedModulesFromDatabase();

        // Populate the ComboBoxes with module and lecturer data from the database
        loadModulesFromDatabase();
        loadLecturersFromDatabase();
    }

    @FXML
    public void assignModule(ActionEvent event) {
        String lecturerName = lecturerNameComboBox.getSelectionModel().getSelectedItem();
        String moduleCode = moduleCodeComboBox.getSelectionModel().getSelectedItem();
        String moduleName = moduleNameComboBox.getSelectionModel().getSelectedItem();

        // Validation checks
        if (lecturerName == null || moduleCode == null || moduleName == null) {
            showAlert("Input Error", "All fields must be filled.");
            return;
        }

        // Connect to the database and insert the assignment data
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO assigned_modules (lecturer_name, module_code, module_name) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, lecturerName);
            statement.setString(2, moduleCode);
            statement.setString(3, moduleName);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Module assigned successfully!");
                loadAssignedModulesFromDatabase();
                clearFields();
            } else {
                showAlert("Error", "Failed to assign module.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while connecting to the database.");
        }
    }

    @FXML
    public void resetFields() {
        clearFields();
    }

    private void clearFields() {
        lecturerNameComboBox.getSelectionModel().clearSelection();
        moduleCodeComboBox.getSelectionModel().clearSelection();
        moduleNameComboBox.getSelectionModel().clearSelection();
    }

    private void loadAssignedModulesFromDatabase() {
        assignedModuleList.clear(); // Clear existing data

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM assigned_modules";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String lecturerName = resultSet.getString("lecturer_name");
                String moduleCode = resultSet.getString("module_code");
                String moduleName = resultSet.getString("module_name");
                assignedModuleList.add(new AssignedModule(lecturerName, moduleCode, moduleName));
            }

            assignedModuleTable.setItems(assignedModuleList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while fetching data from the database.");
        }
    }

    private void loadModulesFromDatabase() {
        ObservableList<String> moduleCodes = FXCollections.observableArrayList();
        ObservableList<String> moduleNames = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT module_code, module_name FROM module";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                moduleCodes.add(resultSet.getString("module_code"));
                moduleNames.add(resultSet.getString("module_name"));
            }

            moduleCodeComboBox.setItems(moduleCodes);
            moduleNameComboBox.setItems(moduleNames);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while fetching module data.");
        }
    }

    private void loadLecturersFromDatabase() {
        ObservableList<String> lecturerNames = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT  name FROM lecturers";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String lecturerName = resultSet.getString("name");
                lecturerNames.add(lecturerName);
            }

            // Populate the ComboBox with the list of lecturer names from the database
            lecturerNameComboBox.setItems(lecturerNames);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while fetching lecturer data.");
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

    // Inner class for assigned modules
    public static class AssignedModule {
        private final SimpleStringProperty lecturerName;
        private final SimpleStringProperty moduleCode;
        private final SimpleStringProperty moduleName;

        public AssignedModule(String lecturerName, String moduleCode, String moduleName) {
            this.lecturerName = new SimpleStringProperty(lecturerName);
            this.moduleCode = new SimpleStringProperty(moduleCode);
            this.moduleName = new SimpleStringProperty(moduleName);
        }

        public String getLecturerName() {
            return lecturerName.get();
        }

        public SimpleStringProperty lecturerNameProperty() {
            return lecturerName;
        }

        public String getModuleCode() {
            return moduleCode.get();
        }

        public SimpleStringProperty moduleCodeProperty() {
            return moduleCode;
        }

        public String getModuleName() {
            return moduleName.get();
        }

        public SimpleStringProperty moduleNameProperty() {
            return moduleName;
        }
    }
}
