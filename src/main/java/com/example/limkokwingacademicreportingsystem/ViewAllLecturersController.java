package com.example.limkokwingacademicreportingsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ViewAllLecturersController {

    // Lecturer Class as an Inner Class
    public static class Lecturer {
        private final StringProperty name;
        private final StringProperty email;


        // Constructor
        public Lecturer(String name, String email) {
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);

        }

        // Getter methods for the properties
        public StringProperty nameProperty() {
            return name;
        }

        public StringProperty emailProperty() {
            return email;
        }


        // Optionally, getters for non-binding purposes
        public String getName() {
            return name.get();
        }

        public String getEmail() {
            return email.get();
        }


    }

    @FXML
    private TableView<Lecturer> lecturerTable;

    @FXML
    private TableColumn<Lecturer, String> lecturerNameColumn;

    @FXML
    private TableColumn<Lecturer, String> lecturerEmailColumn;

    @FXML
    private TableColumn<Lecturer, String> lecturerDepartmentColumn;

    private ObservableList<Lecturer> lecturerList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Initialize the columns with the correct property getters
        lecturerNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        lecturerEmailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());


        // Fetch data from the database
        fetchLecturersFromDatabase();
    }

    private void fetchLecturersFromDatabase() {
        // Replace with your actual database details
        String url = "jdbc:postgresql://localhost:5432/Limkokwing Academic Reporting System";
        String user = "postgres";
        String password = "1234";
        String query = "SELECT id, name, email,gender FROM lecturers";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Clear previous data
            lecturerList.clear();

            // Add data to the ObservableList
            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                lecturerList.add(new Lecturer(name, email));
            }

            // Set the table items to the list of lecturers
            lecturerTable.setItems(lecturerList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
