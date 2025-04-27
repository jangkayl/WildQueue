package com.example.wildqueue.utils;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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

		alert.getDialogPane().setPrefWidth(270);
		alert.getDialogPane().setPrefHeight(150);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);

		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.setResizable(false);

		stage.centerOnScreen();
		stage.setOnShown(e -> {
			stage.setX(stage.getX());
			stage.setY(stage.getY());
		});

		Optional<ButtonType> result = alert.showAndWait();

		result.ifPresent(button -> {
			if (button == ButtonType.OK && fxmlFile != null) {
				navigateToScene(fxmlFile, currentStage, stageTitle);
			}
		});

		return result;
	}

	public static Optional<ButtonType> showAlert(
			Alert.AlertType alertType,
			String title,
			String content,
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

		alert.getDialogPane().setPrefWidth(270);
		alert.getDialogPane().setPrefHeight(150);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);

		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.setResizable(false);

		stage.centerOnScreen();

		return alert.showAndWait();
	}

	private static void navigateToScene(String fxmlFile, Stage currentStage, String stageTitle) {
		try {
			FXMLLoader loader = new FXMLLoader(Utils.class.getResource(fxmlFile));
			Parent root = loader.load();
			Stage stage = (Stage) currentStage.getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.setTitle(stageTitle);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void scrollPaneSetup(ScrollPane scrollPane){
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setPannable(true);
		scrollPane.getStyleClass().add("edge-to-edge");
	}

	public static int comparePriorityNumbers(String num1, String num2) {
		int n1 = Integer.parseInt(num1.replace("Q-", ""));
		int n2 = Integer.parseInt(num2.replace("Q-", ""));

		return Integer.compare(n1, n2);
	}
}
