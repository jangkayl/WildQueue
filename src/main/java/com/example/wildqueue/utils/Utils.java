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
import java.util.Optional;

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

	public static Optional<ButtonType> showAlert(
			Alert.AlertType alertType,
			String title,
			String content,
			String fxmlFile,
			Stage currentStage,
			String stageTitle,
			ButtonType... buttons) {

		Alert alert = new Alert(alertType, content);
		alert.setTitle(title);
		alert.setHeaderText(null);

		if (buttons == null || buttons.length == 0) {
			alert.getButtonTypes().setAll(ButtonType.OK);
		} else {
			alert.getButtonTypes().setAll(buttons);
		}

		if (alertType == Alert.AlertType.WARNING || "LOGOUT".equals(title)) {
			alert.getDialogPane().getStyleClass().add("alert-warning");
		}

		Scene scene = alert.getDialogPane().getScene();
		scene.getStylesheets().add(Utils.class.getResource("/com/example/wildqueue/styles.css").toExternalForm());

		Optional<ButtonType> result = alert.showAndWait();

		result.ifPresent(button -> {
			if (button == ButtonType.OK && fxmlFile != null) {
				navigateToScene(fxmlFile, currentStage, stageTitle);
			}
		});

		return result;
	}

	private static void navigateToScene(String fxmlFile, Stage currentStage, String stageTitle) {
		try {
			FXMLLoader loader = new FXMLLoader(Utils.class.getResource(fxmlFile));
			Parent root = loader.load();
			Stage stage = (Stage) currentStage.getScene().getWindow();
			stage.setScene(new Scene(root, 360, 700));
			stage.setTitle(stageTitle);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
