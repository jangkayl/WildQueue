package com.example.wildqueue.controllers.student;

import com.example.wildqueue.models.User;
import com.example.wildqueue.utils.managers.SessionManager;
import com.example.wildqueue.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StudentProfileController {
	@FXML public ScrollPane scrollPane;
	public Text txtName;
	public Text txtInstitutionalEmail;
	public Button editProfileButton;
	public Label lblLogout;

	@FXML
	public void initialize() {
		Utils.scrollPaneSetup(scrollPane);

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

	@FXML
	private void logout() {
		SessionManager.logout((Stage) lblLogout.getScene().getWindow());
	}
}
