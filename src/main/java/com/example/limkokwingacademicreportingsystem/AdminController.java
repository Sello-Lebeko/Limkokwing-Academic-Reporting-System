package com.example.limkokwingacademicreportingsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.IOException;

public class AdminController {

    @FXML
    private TextField searchField;
    @FXML
    private Button dashboardButton;
    @FXML
    private Button addLecturerButton;
    @FXML
    private Button addAcademicYearButton;
    @FXML
    private Button addSemesterButton;
    @FXML
    private Button addModuleButton;
    @FXML
    private Button addClassButton;
    @FXML
    private Button assignRoleButton;
    @FXML
    private Button assignModuleButton;
    @FXML
    private Button addStudentButton;
    @FXML
    private Button viewProfileButton;
    @FXML
    private Button viewSemestersButton;
    @FXML
    private Button viewLecturersButton;
    @FXML
    private Button viewModulesButton;
    @FXML
    private Button viewPrincipalLecturersButton;
    @FXML
    private Button logoutAsAdminButton;
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private PieChart pieChart;
    @FXML
    private ProgressBar courseProgressBar;
    @FXML
    private PieChart studentLecturerPieChart;

    @FXML
    public void initialize() {
        loadBarChartData();
        loadPieChartData();
        courseProgressBar.setProgress(0.65);

        // Event handling for each button
        dashboardButton.setOnAction(e -> navigateTo("Admin.fxml"));
        addLecturerButton.setOnAction(e -> navigateTo("AddLecturer.fxml"));
        addAcademicYearButton.setOnAction(e -> navigateTo("AddAcademicYear.fxml"));
        addSemesterButton.setOnAction(e -> navigateTo("AddSemester.fxml"));
        addModuleButton.setOnAction(e -> navigateTo("AddModule.fxml"));
        addClassButton.setOnAction(e -> navigateTo("AddClass.fxml"));
        assignRoleButton.setOnAction(e -> navigateTo("AssignRole.fxml"));
        assignModuleButton.setOnAction(e -> navigateTo("AssignModule.fxml"));
        addStudentButton.setOnAction(e -> navigateTo("AddStudents.fxml"));
        viewProfileButton.setOnAction(e -> navigateTo("ViewProfile.fxml"));
        viewSemestersButton.setOnAction(e -> navigateTo("ViewSemesters.fxml"));
        viewLecturersButton.setOnAction(e -> navigateTo("ViewLecturers.fxml"));
        viewModulesButton.setOnAction(e -> navigateTo("ViewModules.fxml"));
        viewPrincipalLecturersButton.setOnAction(e -> navigateTo("ViewPrincipalLecturers.fxml"));

    }

    private void loadBarChartData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Enrollment");

        series.getData().add(new XYChart.Data<>("Semester 1", 200));
        series.getData().add(new XYChart.Data<>("Semester 2", 180));


        barChart.getData().add(series);
    }

    private void loadPieChartData() {
        PieChart.Data java = new PieChart.Data("Java", 30);
        PieChart.Data cpp = new PieChart.Data("C++", 25);
        PieChart.Data python = new PieChart.Data("Python", 20);
        PieChart.Data web = new PieChart.Data("Web Dev", 25);

        pieChart.getData().addAll(java, cpp, python, web);

        // Populate the student vs. lecturer pie chart as well
        PieChart.Data students = new PieChart.Data("Students", 75);
        PieChart.Data lecturers = new PieChart.Data("Lecturers", 25);

        studentLecturerPieChart.getData().addAll(students, lecturers);
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            AnchorPane root = loader.load();

            // Get the stage from the current scene
            Stage stage = (Stage) dashboardButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to load " + fxmlFile);
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    public void goBack() {
        // Load the Admin FXML screen and close the current window
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/limkokwingacademicreportingsystem/Homepage.fxml"));
            AnchorPane adminScreen = loader.load();
            Stage stage = (Stage) logoutAsAdminButton.getScene().getWindow();
            stage.setScene(new Scene(adminScreen));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading the homepage screen.");
        }
    }
}
