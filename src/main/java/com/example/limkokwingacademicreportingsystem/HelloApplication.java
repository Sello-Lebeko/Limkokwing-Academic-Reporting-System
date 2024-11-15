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
            e.printStackTrace();
            showErrorAlert(e.getMessage());
        }
    }

    private void showErrorAlert(String errorMessage) {
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
