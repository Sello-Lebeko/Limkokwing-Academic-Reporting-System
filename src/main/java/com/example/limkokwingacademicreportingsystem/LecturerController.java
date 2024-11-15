package com.example.limkokwingacademicreportingsystem;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class LecturerController {

    @FXML
    private ChoiceBox<String> classChoiceBox;

    @FXML
    private ChoiceBox<String> moduleChoiceBox;

    @FXML
    private ListView<HBox> attendanceListView;

    @FXML
    private ChoiceBox<String> reportClassChoiceBox;

    @FXML
    private ChoiceBox<String> reportModuleChoiceBox;

    @FXML
    private Button markAttendancebtn;

    @FXML
    private TextArea challengesField;

    @FXML
    private Button backbtn;

    @FXML
    private TextArea recommendationsField;


    private Map<String, CheckBox> studentCheckBoxMap = new HashMap<>();

    // Initialize the controller with sample data and event handlers
    @FXML
    public void initialize() {
        loadClassData();
        loadModuleData();
        loadStudentData();
    }

    // Method to connect to the database
    private static Connection connect() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/Limkokwing Academic Reporting System";
        String user = "postgres";
        String password = "1234";
        return DriverManager.getConnection(url, user, password);
    }

    // Method to load class data into the choice boxes
    private void loadClassData() {
        String query = "SELECT name FROM class";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String className = resultSet.getString("name");
                classChoiceBox.getItems().add(className);
                reportClassChoiceBox.getItems().add(className);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadStudentData() {
        String query = "SELECT name FROM students";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String studentName = resultSet.getString("name");

                // Create a CheckBox for each student
                CheckBox presentCheckBox = new CheckBox("Present");
                CheckBox absentCheckBox = new CheckBox("Absent");
                presentCheckBox.setOnAction(e -> {
                    if (presentCheckBox.isSelected()) {
                        absentCheckBox.setSelected(false);
                    }
                });

                // Add a listener to the absent checkbox to mark the student as absent
                absentCheckBox.setOnAction(e -> {
                    if (absentCheckBox.isSelected()) {
                        presentCheckBox.setSelected(false);
                    }
                });

                // Create HBox for student name and checkboxes
                HBox studentHBox = new HBox(10);

                // Left side: Student name
                Label studentLabel = new Label(studentName);

                // Spacer: Push checkboxes to the right
                Region spacer = new Region();
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                // Right side: Present/Absent checkboxes
                HBox checkboxBox = new HBox(10, presentCheckBox, absentCheckBox);

                // Add student name on the left and checkboxes on the right
                studentHBox.getChildren().addAll(studentLabel, spacer, checkboxBox);

                // Add the HBox to the ListView
                attendanceListView.getItems().add(studentHBox);

                // Store the CheckBox in the map for later reference
                studentCheckBoxMap.put(studentName, presentCheckBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // Method to load module data into the moduleChoiceBox
    private void loadModuleData() {
        String query = "SELECT module_name FROM module";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String moduleName = resultSet.getString("module_name");
                moduleChoiceBox.getItems().add(moduleName);
                reportModuleChoiceBox.getItems().add(moduleName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMarkAttendance(MouseEvent event) {
        String selectedClass = classChoiceBox.getValue();
        if (selectedClass == null) {
            System.out.println("Please select a class.");
            return;
        }

        // Iterate through the student names and checkboxes, mark attendance in the database
        for (String studentName : studentCheckBoxMap.keySet()) {
            CheckBox presentCheckBox = studentCheckBoxMap.get(studentName);
            boolean isPresent = presentCheckBox.isSelected();
            markAttendanceInDatabase(studentName, selectedClass, isPresent);
        }
        System.out.println("Attendance has been marked successfully.");
    }

    // Method to insert attendance data into the database
    private void markAttendanceInDatabase(String studentName, String className, boolean isPresent) {
        String query = "INSERT INTO attendance (student_id, class_id, is_present, date) " +
                "SELECT student_id, class_id, ?, CURRENT_TIMESTAMP FROM student, class " +
                "WHERE student.name = ? AND class.name = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setBoolean(1, isPresent);
            statement.setString(2, studentName);
            statement.setString(3, className);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Event handler for submitting the weekly report
    @FXML
    private void handleSubmitReport(MouseEvent event) {
        String selectedClass = reportClassChoiceBox.getValue();
        String selectedModule = reportModuleChoiceBox.getValue();
        String challenges = challengesField.getText();
        String recommendations = recommendationsField.getText();

        if (selectedClass == null || selectedModule == null || challenges.isEmpty() || recommendations.isEmpty()) {
            System.out.println("Please complete all fields before submitting.");
            return;
        }

        // Save the report to the database
        submitReportToDatabase(selectedClass, selectedModule, challenges, recommendations);

        // Clear fields after submission
        challengesField.clear();
        recommendationsField.clear();
        System.out.println("Report has been submitted successfully.");
    }

    // Method to insert report data into the database
    private void submitReportToDatabase(String className, String moduleName, String challenges, String recommendations) {
        String query = "INSERT INTO reports (class_name, module_name, challenges, recommendations, report_date) " +
                "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, className);
            statement.setString(2, moduleName);
            statement.setString(3, challenges);
            statement.setString(4, recommendations);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
}
