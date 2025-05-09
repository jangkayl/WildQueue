package com.example.wildqueue.controllers.student;

import com.example.wildqueue.models.User;
import com.example.wildqueue.utils.managers.SessionManager;
import com.example.wildqueue.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class StudentProfileController {
	@FXML public ScrollPane scrollPane;
	@FXML private Text txtName;
	@FXML private Text txtInstitutionalEmail;
	@FXML private Button editProfileButton;
	@FXML private Label lblLogout;
	@FXML private Label lblHistory;
	private StudentMainController mainController;

	@FXML
	public void initialize() {
		Utils.scrollPaneSetup(scrollPane);
		loadUserData();

		editProfileButton.setOnAction(event -> mainController.showEditProfile());
	}

	public void setMainController(StudentMainController mainController) {
		this.mainController = mainController;
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

	@FXML
	private void goToHistory(){
		if(mainController != null){
			mainController.navigateToHistory();
		}
	}

	@FXML
	private void logout() {
		SessionManager.logout((Stage) lblLogout.getScene().getWindow());
	}

	@FXML
	private void editProfile(ActionEvent actionEvent) {
		mainController.showEditProfile();
	}
}
