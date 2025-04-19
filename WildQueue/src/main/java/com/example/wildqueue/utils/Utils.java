package com.example.wildqueue.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
	public static String hashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = md.digest(password.getBytes());

			StringBuilder sb = new StringBuilder();
			for (byte b : hashedBytes) {
				sb.append(String.format("%02x", b));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void showAlert(Alert.AlertType alertType, String title, String content, String fxmlFile, Stage currentStage, String stageTitle) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);

		Scene scene = alert.getDialogPane().getScene();
		scene.getStylesheets().add(Utils.class.getResource("/com/example/wildqueue/styles.css").toExternalForm());

		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				try {
					if (fxmlFile != null && !fxmlFile.isEmpty()) {
						FXMLLoader loader = new FXMLLoader(Utils.class.getResource(fxmlFile));
						Parent root = loader.load();

						currentStage.setScene(new Scene(root));
						currentStage.setTitle(stageTitle);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
