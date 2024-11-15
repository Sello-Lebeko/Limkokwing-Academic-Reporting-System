package com.example.limkokwingacademicreportingsystem;

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

public class AddStudentsController {

    @FXML
    private TextField name_txt_field;
    @FXML
    private TextField id_txt_field;
    @FXML
    private TextField email_txt_field;
    @FXML
    private TextField gender_txt_field;

    @FXML
    private TextField age_txt_field;
    @FXML
    private TextField assign_txt_field;
    @FXML
    private Button addStudentBtn;

    @FXML
    private Button resetBtn;

    @FXML
    private Button back_btn;


    @FXML
    private TableView<Student> studentTable;

    @FXML
    private TableColumn<Student, String> colName;

    @FXML
    private TableColumn<Student, String> colID;
    @FXML
    private TableColumn<Student, String> colEmail;
    @FXML
    private TableColumn<Student, String> colGender;

    @FXML
    private TableColumn<Student, String> colAge;

    @FXML
    private TableColumn<Student, String> colClass;

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Limkokwing Academic Reporting System";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "1234";

    // ObservableList to hold student data
    private ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(cellData -> cellData.getValue().studentNameProperty());
        colID.setCellValueFactory(cellData -> cellData.getValue().studentIDProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colGender.setCellValueFactory(cellData -> cellData.getValue().genderProperty());
        colAge.setCellValueFactory(cellData -> cellData.getValue().ageProperty());
        colClass.setCellValueFactory(cellData -> cellData.getValue().classProperty());

        loadStudentsFromDatabase();
    }

    @FXML
    public void addStudent() {
        String studentName = name_txt_field.getText();
        String studentID = id_txt_field.getText();
        String email = email_txt_field.getText();
        String gender = gender_txt_field.getText();
        String age = age_txt_field.getText();
        String studentClass = assign_txt_field.getText();

        // Validation checks
        if (studentID.isEmpty() || studentName.isEmpty() || age.isEmpty() || gender.isEmpty() || studentClass.isEmpty() || email.isEmpty()) {
            showAlert("Input Error", "All fields (ID, Name, Age, Gender, Class, Email) must be filled.");
            return;
        }

        // Connect to the database and insert the student data
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO students (name, student_id, email, gender, age, assigned_class) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, studentName);
            statement.setString(2, studentID);
            statement.setString(3, email);
            statement.setString(4, gender);
            statement.setString(5, age);
            statement.setString(6, studentClass);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Student added successfully!");
                loadStudentsFromDatabase();
                clearFields();

                // After adding, assign the class to the student
                assignClass(studentID, studentClass);
            } else {
                showAlert("Error", "Failed to add student.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while connecting to the database.");
        }
    }

    private void assignClass(String studentID, String studentClass) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE students SET assigned_class = ? WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, studentClass);
            statement.setString(2, studentID);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Class Assignment", "Class assigned successfully to student.");
                loadStudentsFromDatabase(); // Reload the table
            } else {
                showAlert("Error", "Failed to assign class.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while assigning the class.");
        }
    }

    @FXML
    public void resetFields() {
        clearFields();
    }

    private void clearFields() {
        name_txt_field.clear();
        id_txt_field.clear();
        email_txt_field.clear();
        gender_txt_field.clear();
        age_txt_field.clear();
        assign_txt_field.clear();
    }

    private void loadStudentsFromDatabase() {
        studentList.clear(); // Clear existing data

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM students";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String studentName = resultSet.getString("name");
                String studentID = resultSet.getString("student_id");
                String email = resultSet.getString("email");
                String gender = resultSet.getString("gender");
                String age = resultSet.getString("age");
                String studentClass = resultSet.getString("assigned_class");

                studentList.add(new Student(studentName, studentID, email, gender, age, studentClass));
            }

            studentTable.setItems(studentList); // Set the items for the TableView
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

    public static class Student {
        private final SimpleStringProperty studentID;
        private final SimpleStringProperty studentName;
        private final SimpleStringProperty age;
        private final SimpleStringProperty gender;
        private final SimpleStringProperty studentClass;
        private final SimpleStringProperty email;

        public Student(String studentName, String studentID, String email, String gender, String age, String studentClass) {
            this.studentName = new SimpleStringProperty(studentName);
            this.studentID = new SimpleStringProperty(studentID);
            this.email = new SimpleStringProperty(email);
            this.gender = new SimpleStringProperty(gender);
            this.age = new SimpleStringProperty(age);
            this.studentClass = new SimpleStringProperty(studentClass);
        }

        public SimpleStringProperty studentNameProperty() {
            return studentName;
        }

        public SimpleStringProperty studentIDProperty() {
            return studentID;
        }

        public SimpleStringProperty emailProperty() {
            return email;
        }

        public SimpleStringProperty genderProperty() {
            return gender;
        }

        public SimpleStringProperty ageProperty() {
            return age;
        }

        public SimpleStringProperty classProperty() {
            return studentClass;
        }
    }
}
