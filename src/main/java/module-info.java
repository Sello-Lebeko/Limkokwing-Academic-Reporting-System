module com.example.limkokwingacademicreportingsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.limkokwingacademicreportingsystem to javafx.fxml;
    exports com.example.limkokwingacademicreportingsystem;
}