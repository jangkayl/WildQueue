module com.example.oopcapstone {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.oopcapstone to javafx.fxml;
    exports com.example.oopcapstone;
}