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

public class AddModuleController {

    @FXML
    private TextField module_code_txt_field; // TextField for module code
    @FXML
    private TextField module_name_text_field; // TextField for module name

    @FXML
    private Button add_btn; // Button to add module
    @FXML
    private Button reset_btn; // Button to reset fields
    @FXML
    private Button back_btn; // Button to go back

    @FXML
    private TableView<Module> moduleTable;
    @FXML
    private TableColumn<Module, String> moduleCodeColumn;
    @FXML
    private TableColumn<Module, String> moduleNameColumn;

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Limkokwing Academic Reporting System";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "1234";

    // ObservableList to hold module data
    private ObservableList<Module> moduleList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize the TableView columns
        moduleCodeColumn.setCellValueFactory(cellData -> cellData.getValue().moduleIdProperty());
        moduleNameColumn.setCellValueFactory(cellData -> cellData.getValue().moduleNameProperty());

        // Load existing modules from the database
        loadModulesFromDatabase();
    }

    @FXML
    public void addModule() {
        String moduleCode = module_code_txt_field.getText();
        String moduleName = module_name_text_field.getText();

        // Validation check
        if (moduleCode.isEmpty() || moduleName.isEmpty()) {
            showAlert("Input Error", "All fields must be filled.");
            return;
        }

        // Connect to the database and insert the module data
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO module (module_code, module_name) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, moduleCode);
            statement.setString(2, moduleName);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Module added successfully!");
                loadModulesFromDatabase(); // Reload the table with the new module
                clearFields(); // Clear fields after successful insertion
            } else {
                showAlert("Error", "Failed to add module.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while connecting to the database.");
        }
    }

    private void clearFields() {
        module_code_txt_field.clear();
        module_name_text_field.clear();
    }

    private void loadModulesFromDatabase() {
        moduleList.clear(); // Clear existing data

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM module";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String moduleId = resultSet.getString("module_code");
                String moduleName = resultSet.getString("module_name");

                moduleList.add(new Module(moduleId, moduleName));
            }

            moduleTable.setItems(moduleList); // Set items for TableView
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
    private void resetFields(ActionEvent event) {
        clearFields();
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


    public static class Module {
        private final SimpleStringProperty moduleId;
        private final SimpleStringProperty moduleName;

        public Module(String moduleId, String moduleName) {
            this.moduleId = new SimpleStringProperty(moduleId);
            this.moduleName = new SimpleStringProperty(moduleName);
        }

        public String getModuleId() { return moduleId.get(); }
        public String getModuleName() { return moduleName.get(); }

        public SimpleStringProperty moduleIdProperty() { return moduleId; }
        public SimpleStringProperty moduleNameProperty() { return moduleName; }
    }
}
