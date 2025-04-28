package com.example.wildqueue.utils;

import com.example.wildqueue.models.User;
import com.example.wildqueue.services.QueueUpdaterService;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class SessionManager {
	private static User currentUser;

	private SessionManager() {}

	public static void setCurrentUser(User user) {
		Serialize.serialize(user,"user.ser");
		currentUser = user;
	}

	public static User getCurrentUser() { return currentUser; }

	public static void clearSession() {
		Serialize.serialize(null, "user.ser");
		currentUser = null;
	}

	public static void logout(Stage stage) {
		Utils.showAlert(
				Alert.AlertType.WARNING,
				"LOGOUT",
				"Are you sure you want to logout?",
				"/com/example/wildqueue/login-page.fxml",
				stage,
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