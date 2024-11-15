package com.example.limkokwingacademicreportingsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/limkokwingacademicreportingsystem/SignUp.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 971, 647);
            stage.setTitle("Limkokwing Academic Reporting System");
            stage.setScene(scene);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();  // Print the error stack trace for debugging
            showErrorAlert(e.getMessage());  // Show alert with error message
        }
    }

    private void showErrorAlert(String errorMessage) {
        // Create an alert box to show the error message
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred while loading the application");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
