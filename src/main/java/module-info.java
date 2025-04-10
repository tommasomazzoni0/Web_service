module com.example.web_service {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.web_service to javafx.fxml;
    exports com.example.web_service;
}