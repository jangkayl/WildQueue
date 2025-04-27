package com.example.wildqueue.controllers.teller;

import com.example.wildqueue.dao.TellerWindowDAO;
import com.example.wildqueue.models.User;
import com.example.wildqueue.services.QueueUpdaterService;
import com.example.wildqueue.utils.SessionManager;
import com.example.wildqueue.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TellerHomepageController {
	public Label tellerNameLabel;
	public Button logoutButton;
	public Text tellerNameSidebar;
	public Text windowNumberText;
	public Button callButton;
	public Button completeButton;
	public Button transferButton;
	public Button windowToggleButton;
	public Button priorityButton;
	public Button recallButton;
	public Button notifyButton;
	public Button viewHistoryButton;
	public Text currentNumberText;
	public Text currentStudentText;
	public Text waitingCountText;
	public Text priorityCountText;
	public Text estimatedWaitText;
	public Text servedTodayText;
	public Text avgTimeText;
	public Text windowStatusText;
	public Button refreshActivityButton;
	public VBox activityContainer;

	@FXML
	public void initialize() {
		loadUserData();
	}

	private void loadUserData() {
		User currentUser = SessionManager.getCurrentUser();

		if (currentUser != null) {
			tellerNameLabel.setText("Teller: " + currentUser.getName());
			tellerNameSidebar.setText(currentUser.getName());
		} else {
			tellerNameLabel.setText("Guest");
			tellerNameSidebar.setText("Guest");
		}
	}

	@FXML
	private void logout(){
		Utils.showAlert(
				Alert.AlertType.WARNING,
				"LOGOUT",
				"Are you sure you want to logout?",
				"/com/example/wildqueue/login-page.fxml",
				(Stage) logoutButton.getScene().getWindow(),
				"Login",
				ButtonType.OK,
				ButtonType.CANCEL
		).ifPresent(response -> {
			if (response == ButtonType.OK) {
				QueueUpdaterService.getInstance().stopUpdateThread();
				SessionManager.clearSession();
			}
		});
	}
}
