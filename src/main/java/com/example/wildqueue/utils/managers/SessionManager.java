package com.example.wildqueue.utils.managers;

import com.example.wildqueue.dao.UserDAO;
import com.example.wildqueue.models.User;
import com.example.wildqueue.services.QueueUpdaterService;
import com.example.wildqueue.services.TransactionUpdaterService;
import com.example.wildqueue.services.WindowUpdaterService;
import com.example.wildqueue.utils.Serialize;
import com.example.wildqueue.utils.Utils;
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
				TransactionUpdaterService.getInstance().stopUpdateThread();
				WindowUpdaterService.getInstance().stopUpdateThread();
				SessionManager.clearSession();
			}
		});
	}

	public static void refreshCurrentUser() {
		if (currentUser != null) {
			currentUser = UserDAO.getUserByInstitutionalId(currentUser.getInstitutionalId());
		}
	}
}