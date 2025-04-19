module com.example.WildQueue {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.wildqueue to javafx.fxml;
    exports com.example.wildqueue;
    exports com.example.wildqueue.controllers;
    opens com.example.wildqueue.controllers to javafx.fxml;
}