package com.example.wildqueue.controllers.student;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

public class HistoryController {
	@FXML private ScrollPane scrollPane;

	@FXML
	public void initialize() {
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setPannable(true);

		scrollPane.getStyleClass().add("edge-to-edge");
	}

	public void goToHistory(ActionEvent actionEvent) {
    }

    public void goToProfile(ActionEvent actionEvent) {
    }

    public void goToHome(ActionEvent actionEvent) {
    }
}
