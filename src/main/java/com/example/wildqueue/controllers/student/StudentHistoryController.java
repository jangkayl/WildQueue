package com.example.wildqueue.controllers.student;

import com.example.wildqueue.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

public class StudentHistoryController {
	@FXML private ScrollPane scrollPane;

	@FXML
	public void initialize() {
		Utils.scrollPaneSetup(scrollPane);

		scrollPane.getStyleClass().add("edge-to-edge");
	}

	public void goToHistory(ActionEvent actionEvent) {
    }

    public void goToProfile(ActionEvent actionEvent) {
    }

    public void goToHome(ActionEvent actionEvent) {
    }
}
