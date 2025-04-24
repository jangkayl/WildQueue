package com.example.wildqueue.controllers;

import com.example.wildqueue.CRUD;
import com.example.wildqueue.models.User;
import com.example.wildqueue.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class ProfileController {
	@FXML public ScrollPane scrollPane;
	public Text txtName;
	public Text txtInstitutionalEmail;
	public Button editProfileButton;

	@FXML
	public void initialize() {
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setPannable(true);

		scrollPane.getStyleClass().add("edge-to-edge");

		loadUserData();
	}

	private void loadUserData() {
		User currentUser = SessionManager.getCurrentUser();

		if (currentUser != null) {
			txtName.setText(currentUser.getName());
			txtInstitutionalEmail.setText(currentUser.getInstitutionalId());
		} else {
			txtName.setText("Guest");
			txtInstitutionalEmail.setText("Not logged in");
		}
	}
}
