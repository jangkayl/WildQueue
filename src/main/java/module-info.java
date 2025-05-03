module com.example.WildQueue {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;
	requires java.desktop;

	opens com.example.wildqueue to javafx.fxml;
	opens com.example.wildqueue.models to javafx.fxml;
	opens com.example.wildqueue.controllers to javafx.fxml;
	opens com.example.wildqueue.controllers.admin to javafx.fxml;
	opens com.example.wildqueue.controllers.teller to javafx.fxml;
	opens com.example.wildqueue.controllers.student to javafx.fxml;

	exports com.example.wildqueue;
	exports com.example.wildqueue.controllers;
	exports com.example.wildqueue.models;
	exports com.example.wildqueue.utils to javafx.fxml;
	exports com.example.wildqueue.controllers.student;
	exports com.example.wildqueue.services to javafx.fxml;
	exports com.example.wildqueue.controllers.teller;
	exports com.example.wildqueue.controllers.admin;
	exports com.example.wildqueue.utils.managers to javafx.fxml;
}