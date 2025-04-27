package com.example.wildqueue.controllers.student;

import com.example.wildqueue.models.User;
import com.example.wildqueue.utils.SessionManager;
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

		scrollPane.getStyleClass().add("edge-to-edge");

		loadUserData();
	}

	public void loadUserData() {
		User currentUser = SessionManager.getCurrentUser();

		if (currentUser != null) {
			txtName.setText(currentUser.getName());
			txtInstitutionalEmail.setText(currentUser.getInstitutionalId());
		} else {
			txtName.setText("Guest");
			txtInstitutionalEmail.setText("Not logged in");
		}
	}

	public void logout() {
		Utils.showAlert(
				Alert.AlertType.WARNING,
				"LOGOUT",
				"Are you sure you want to logout?",
				"/com/example/wildqueue/login-page.fxml",
				(Stage) lblLogout.getScene().getWindow(),
				"Login",
				ButtonType.OK,
				ButtonType.CANCEL
		).ifPresent(response -> {
			if (response == ButtonType.OK) {
				SessionManager.clearSession();
			}
		});
	}

}
